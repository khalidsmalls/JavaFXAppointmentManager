package com.smalls.javafxappointmentmanager;

import com.smalls.javafxappointmentmanager.utils.CustomLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class MainApplication extends Application {

    private static Connection conn;

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

    @Override
    public void init() throws Exception {
        CustomLogger.initialize("/logfile.log");
        try {
            Properties properties = new Properties();
            InputStream input = MainApplication.class
                    .getClassLoader()
                    .getResourceAsStream(
                            "config.properties"
                    );
            properties.load(input);

            String URL = "jdbc:postgresql://localhost:5432/javafx_appointment_manager";
            conn = DriverManager.getConnection(
                    URL,
                    properties.getProperty("dbUser"),
                    properties.getProperty("dbPassword")
            );
        } catch (SQLException e) {
            CustomLogger.logMessage(
                    Level.SEVERE,
                    "Database connection error: " + e
            );
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) {
        launch();
    }
}