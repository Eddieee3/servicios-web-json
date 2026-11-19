package com.desarrollo.serviciosweb.service;

import com.desarrollo.serviciosweb.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Consume la API REST de Supabase (tabla "customers") con HttpClient y procesa el JSON con Jackson.
 * Es bloqueante: debe llamarse fuera del hilo de JavaFX.
 */
public class UsuarioService {
    public static final String URL_USUARIOS =
            "https://sqpvqkkdfgtzdgrjrgxs.supabase.co/rest/v1/customers?order=id.asc";
    // Clave publicable de Supabase (pensada para clientes; el acceso lo limitan las politicas RLS)
    private static final String API_KEY = "sb_publishable_vIJdi0L7Kd2XtbzaTaFSgg_xvbo3eAf";
    private static final Duration TIMEOUT_CONEXION = Duration.ofSeconds(10);
    private static final Duration TIMEOUT_RESPUESTA = Duration.ofSeconds(15);

    private final HttpClient cliente;
    private final URI uri;
    private final ObjectMapper mapper = new ObjectMapper();

    public UsuarioService() {
        this(URL_USUARIOS);
    }

    public UsuarioService(String url) {
        this.uri = URI.create(url);
        this.cliente = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT_CONEXION)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * @throws IOException si falla la conexion, se agota el tiempo, el codigo HTTP no es 2xx
     *                     o el JSON no tiene el formato esperado.
     */
    public List<Usuario> obtenerUsuarios() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(TIMEOUT_RESPUESTA)
                .header("Accept", "application/json")
                .header("apikey", API_KEY)
                .GET()
                .build();

        HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

        int estado = response.statusCode();
        if (estado < 200 || estado >= 300) {
            throw new IOException("La API respondio con el codigo HTTP " + estado);
        }
        return parsearUsuarios(response.body());
    }

    /** Convierte el cuerpo JSON de /customers en la lista de usuarios. */
    public List<Usuario> parsearUsuarios(String json) throws IOException {
        if (json == null || json.isBlank()) {
            throw new IOException("La API devolvio una respuesta vacia");
        }
        JsonNode datos;
        try {
            datos = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IOException("La respuesta no es un JSON valido", e);
        }
        if (datos == null || !datos.isArray()) {
            throw new IOException("Formato inesperado: se esperaba un arreglo JSON de usuarios");
        }

        List<Usuario> usuarios = new ArrayList<>();
        for (JsonNode dato : datos) {
            if (!dato.isObject()) {
                throw new IOException("Formato inesperado: un elemento de usuario no es un objeto JSON");
            }
            JsonNode id = dato.path("id");
            Usuario usuario = new Usuario();
            usuario.setId(id.canConvertToInt() ? id.asInt() : null);
            usuario.setNombre(dato.path("first_name").asText(""));
            usuario.setApellido(dato.path("last_name").asText(""));
            usuario.setEmail(dato.path("email").asText(""));
            usuario.setTelefono(dato.path("phone").asText(""));
            usuario.setCiudad(dato.path("city").asText(""));
            usuario.setPais(dato.path("country").asText(""));
            usuario.setEstado(dato.path("status").asText(""));
            usuarios.add(usuario);
        }
        return usuarios;
    }
}
