package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.UserDAO;
import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.User;
import com.smalls.javafxappointmentmanager.utils.CustomLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class LoginController implements Initializable {

    @FXML
    private Label dateTimeLabel;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateTimeLabel.setText(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .format(LocalDateTime.now()) + " " + ZoneId.systemDefault()
        );
    }

    @FXML
    private void onSignIn() {
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText().trim();

        try {
            User user = UserDAO.getInstance()
                    .authenticateUser(
                            username,
                            password
                    );
            if (user != null) {
                //successful login
                CustomLogger.logMessage(
                        Level.INFO,
                        String.format(
                                "User \"%s\" successful login",
                                username
                        )
                );

                FXMLLoader loader = new FXMLLoader();
                URL styleSheet = getClass().getResource("styles.css");
                URL dashboardView = getClass().getResource("view/dashboard-view.fxml");
                loader.setLocation(dashboardView);
                //loader.setControllerFactory(c -> new DashboardController(user));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                if (styleSheet != null) {
                    scene.getStylesheets().add(styleSheet.toExternalForm());
                }
                Stage stage = new Stage();
                stage.setTitle("Dashboard");
                stage.setScene(scene);
                stage.show();

            } else {
                //failed login
                CustomLogger.logMessage(
                        Level.WARNING,
                        String.format(
                                "User \"%s\" failed login",
                                username
                        )
                );

                new Alert(Alert.AlertType.ERROR,
                        "Login Failed: Invalid Credentials")
                        .showAndWait();
            }

        } catch (SQLException | IOException e) {
            CustomLogger.logMessage(
                    Level.SEVERE,
                    String.format(
                            "There was an error loading the resource: %s",
                            e
                    )
            );
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onClose(ActionEvent e) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you would like to close the application?"
        );
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
        }
    }
}