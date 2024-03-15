package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.UserDAO;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

        String nowDateTime = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .format(LocalDateTime.now());

        dateTimeLabel.setText(nowDateTime);
    }

    @FXML
    private void onSignIn() {
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText().trim();
        String className = getClass().getName();
        UserDAO userDAO = UserDAO.getInstance();

        try {
            User user = userDAO.authenticateUser(username, password);
            String logMsg;
            if (user != null) { //successful login
                logMsg = String.format(
                        "User \"%s\" successful login",
                        username
                );
                CustomLogger.log(className, Level.INFO, logMsg);

                loadDashboardView(user);
                //TODO close login view
            } else { //failed login
                logMsg = String.format("User \"%s\" failed login", username);
                CustomLogger.log(className, Level.WARNING, logMsg);

                new Alert(Alert.AlertType.ERROR,
                        "Login Failed: Invalid Credentials")
                        .showAndWait();
            }

        } catch (SQLException | IOException e) {
            String logMsg = String.format(
                    "There was an error loading the resource: %s",
                    e.getMessage()
            );
            CustomLogger.log(className, Level.SEVERE, logMsg);
            //TODO how to best handle the exception?
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

    private void loadDashboardView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL styleSheet = getClass()
                .getResource("/com/smalls/javafxappointmentmanager/styles.css");
        URL dashboardView = getClass()
                .getResource("/com/smalls/javafxappointmentmanager/view/dashboard-view.fxml");
        loader.setLocation(dashboardView);
        loader.setControllerFactory(param -> new DashboardController((user)));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        if (styleSheet != null) {
            scene.getStylesheets().add(styleSheet.toExternalForm());
        }
        Stage stage = new Stage();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.setMinHeight(775);
        stage.setMinWidth(1300);
        stage.show();
    }
}