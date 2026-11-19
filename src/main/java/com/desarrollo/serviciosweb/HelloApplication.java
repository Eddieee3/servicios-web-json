package com.desarrollo.serviciosweb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 560);
        stage.setTitle("Clientes - Supabase");
        stage.setMinWidth(720);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }
}
