package com.desarrollo.serviciosweb.service;

import com.desarrollo.serviciosweb.model.Usuario;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    // Misma estructura que /rest/v1/customers de Supabase (valores sinteticos)
    private static final String JSON_USUARIOS = """
            [
              {
                "id": 1,
                "first_name": "Ana",
                "last_name": "Perez",
                "email": "ana@example.com",
                "phone": "+50555500101",
                "date_of_birth": "1990-01-01",
                "country": "Nicaragua",
                "city": "Managua",
                "address": "Calle 1",
                "annual_income": 45000.00,
                "credit_score": 700,
                "has_unpaid_debt": false,
                "unpaid_debt_amount": 0.00,
                "status": "active",
                "created_at": "2026-11-17T16:14:20.965102+00:00",
                "updated_at": "2026-11-17T16:14:20.965102+00:00"
              },
              { "id": 2, "first_name": "Luis", "last_name": "Gomez", "email": "luis@example.com" }
            ]
            """;

    private HttpServer servidor;

    @AfterEach
    void detenerServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void parseaTodosLosCamposDelUsuario() throws IOException {
        List<Usuario> usuarios = new UsuarioService().parsearUsuarios(JSON_USUARIOS);

        assertEquals(2, usuarios.size());
        Usuario u = usuarios.get(0);
        assertEquals(1, u.getId());
        assertEquals("Ana", u.getNombre());
        assertEquals("Perez", u.getApellido());
        assertEquals("ana@example.com", u.getEmail());
        assertEquals("+50555500101", u.getTelefono());
        assertEquals("Managua", u.getCiudad());
        assertEquals("Nicaragua", u.getPais());
        assertEquals("active", u.getEstado());
    }

    @Test
    void camposAusentesQuedanVacios() throws IOException {
        Usuario u = new UsuarioService().parsearUsuarios(JSON_USUARIOS).get(1);

        assertEquals(2, u.getId());
        assertEquals("", u.getTelefono());
        assertEquals("", u.getCiudad());
        assertEquals("", u.getPais());
        assertEquals("", u.getEstado());
    }

    @Test
    void jsonInvalidoLanzaIOException() {
        IOException e = assertThrows(IOException.class,
                () -> new UsuarioService().parsearUsuarios("{no es json"));
        assertTrue(e.getMessage().contains("JSON valido"));
    }

    @Test
    void jsonQueNoEsArregloLanzaIOException() {
        IOException e = assertThrows(IOException.class,
                () -> new UsuarioService().parsearUsuarios("{\"id\": 1}"));
        assertTrue(e.getMessage().contains("arreglo"));
    }

    @Test
    void respuestaVaciaLanzaIOException() {
        assertThrows(IOException.class, () -> new UsuarioService().parsearUsuarios("  "));
    }

    @Test
    void obtenerUsuariosConsumeRespuestaHttp200() throws Exception {
        String url = iniciarServidor(200, JSON_USUARIOS);

        List<Usuario> usuarios = new UsuarioService(url).obtenerUsuarios();

        assertEquals(2, usuarios.size());
        assertEquals("Managua", usuarios.get(0).getCiudad());
    }

    @Test
    void obtenerUsuariosRechazaCodigoHttpDeError() throws Exception {
        String url = iniciarServidor(500, "error");

        IOException e = assertThrows(IOException.class, () -> new UsuarioService(url).obtenerUsuarios());
        assertTrue(e.getMessage().contains("500"));
    }

    @Test
    void obtenerUsuariosFallaSinConexion() throws Exception {
        // Puerto local sin servidor escuchando
        String url = iniciarServidor(200, "[]");
        servidor.stop(0);
        servidor = null;

        assertThrows(IOException.class, () -> new UsuarioService(url).obtenerUsuarios());
    }

    private String iniciarServidor(int estado, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/customers", intercambio -> {
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(estado, bytes.length);
            try (OutputStream os = intercambio.getResponseBody()) {
                os.write(bytes);
            }
        });
        servidor.start();
        return "http://127.0.0.1:" + servidor.getAddress().getPort() + "/customers";
    }
}
