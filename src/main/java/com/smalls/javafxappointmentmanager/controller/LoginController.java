package com.smalls.javafxappointmentmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

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
    private void onSignIn() {}

    @FXML
    private void onClose() {}
}