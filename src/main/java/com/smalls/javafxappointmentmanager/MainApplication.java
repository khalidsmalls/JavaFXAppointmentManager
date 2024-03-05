package com.smalls.javafxappointmentmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        URL styleSheet = MainApplication.class.getResource("styles.css");
        if (styleSheet != null) {
            scene.getStylesheets().add(styleSheet.toExternalForm());
        }

        stage.setTitle("Sign In");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}