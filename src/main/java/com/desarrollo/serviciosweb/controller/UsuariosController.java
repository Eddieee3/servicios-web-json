package com.desarrollo.serviciosweb.controller;

import com.desarrollo.serviciosweb.model.Usuario;
import com.desarrollo.serviciosweb.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import java.util.List;

public class UsuariosController {
    @FXML
    private Button btnConsultar;
    @FXML
    private Label lblEstado;
    @FXML
    private TableView<Usuario> tablaUsuarios;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void initialize() {
        tablaUsuarios.setPlaceholder(new Label("Sin datos. Presione \"Consultar usuarios\"."));
    }

    @FXML
    private void onConsultarUsuarios() {
        // La solicitud HTTP corre en un hilo de fondo; los manejadores del Task se ejecutan en el hilo de JavaFX
        Task<List<Usuario>> tarea = new Task<>() {
            @Override
            protected List<Usuario> call() throws Exception {
                return usuarioService.obtenerUsuarios();
            }
        };

        btnConsultar.setDisable(true);
        lblEstado.setText("Consultando usuarios...");

        tarea.setOnSucceeded(e -> {
            List<Usuario> usuarios = tarea.getValue();
            tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            lblEstado.setText("Se cargaron " + usuarios.size() + " usuarios.");
            btnConsultar.setDisable(false);
        });
        tarea.setOnFailed(e -> {
            lblEstado.setText("Error: " + describirError(tarea.getException()));
            btnConsultar.setDisable(false);
        });

        Thread hilo = new Thread(tarea, "consulta-usuarios");
        hilo.setDaemon(true);
        hilo.start();
    }

    private static String describirError(Throwable error) {
        if (error instanceof HttpTimeoutException) {
            return "tiempo de espera agotado al contactar la API.";
        }
        if (error instanceof ConnectException || error instanceof UnknownHostException) {
            return "no se pudo conectar con la API. Verifique su conexion a Internet.";
        }
        if (error instanceof IOException && error.getMessage() != null) {
            return error.getMessage();
        }
        return "fallo inesperado (" + error.getClass().getSimpleName() + ").";
    }
}
