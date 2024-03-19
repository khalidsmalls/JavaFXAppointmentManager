package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.AppointmentDAO;
import com.smalls.javafxappointmentmanager.DAO.ClientDAO;
import com.smalls.javafxappointmentmanager.DAO.ContactDAO;
import com.smalls.javafxappointmentmanager.DAO.UserDAO;
import com.smalls.javafxappointmentmanager.model.Appointment;
import com.smalls.javafxappointmentmanager.model.Client;
import com.smalls.javafxappointmentmanager.model.Contact;
import com.smalls.javafxappointmentmanager.model.User;
import com.smalls.javafxappointmentmanager.utils.AppointmentValidator;
import com.smalls.javafxappointmentmanager.utils.BusinessHoursGenerator;
import com.smalls.javafxappointmentmanager.utils.ComboCellFactoryUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.util.Callback;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class AppointmentViewController implements Initializable {

    @FXML
    private Label appointmentViewLabel;

    @FXML
    private TextField idInput;

    @FXML
    private TextField descriptionInput;

    @FXML
    private TextField locationInput;

    @FXML
    private TextField typeInput;

    @FXML
    private ComboBox<User> userCombo;

    @FXML
    private ComboBox<Client> clientCombo;

    @FXML
    private ComboBox<Contact> contactCombo;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<LocalTime> startCombo;

    @FXML
    private ComboBox<LocalTime> endCombo;

    private ObservableMap<Integer, User> users;

    private ObservableMap<Integer, Client> clients;

    private ObservableMap<Integer, Contact> contacts;

    private Appointment appointment;

    private final UnaryOperator<TextFormatter.Change> inputFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 50) {
            return null;
        }
        if (newText.matches("([0-9a-zA-Z\\- ']*)")) {
            return change;
        }
        return null;
    };

    public AppointmentViewController() {}

    public AppointmentViewController(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idInput.setEditable(false);
        try {
            users = UserDAO.getInstance().getAll();
            clients = ClientDAO.getInstance().getAll();
            contacts = ContactDAO.getInstance().getAll();
            initComboBoxes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (appointment != null) {
            initModifyAppointment(appointment);
        }
        setTextFormatters();
    }

    @FXML
    private void onSave(ActionEvent e) throws SQLException {
        AppointmentDAO appointmentDAO = AppointmentDAO.getInstance();
        String description, location, type;
        int appointmentId, clientId, userId, contactId;
        LocalTime start, end;
        LocalDate date;
        Button saveBtn = ((Button) e.getSource());
        Stage stage = (Stage) saveBtn.getScene().getWindow();

        if (validateInputs()) {
            if (appointment != null) {
                appointmentId = appointment.getId();
            } else {
                appointmentId = -1;
            }
            description = descriptionInput.getText().trim();
            location = locationInput.getText().trim();
            type = typeInput.getText().trim();
            clientId = contactCombo.getValue().getId();
            userId = userCombo.getValue().getId();
            date = datePicker.getValue();
            contactId = contactCombo.getValue().getId();
            start = startCombo.getValue();
            end = endCombo.getValue();

            if (end.isBefore(start) || end.equals(start)) {
                String alertMsg = "start time must be before end time";
                new Alert(Alert.AlertType.ERROR, alertMsg).showAndWait();
                return;
            }

            ZoneOffset offset = ZoneId.systemDefault()
                    .getRules()
                    .getOffset(Instant.now());

            OffsetDateTime startOdt = OffsetDateTime.of(
                    date,
                    start,
                    offset
            );
            OffsetDateTime endOdt = OffsetDateTime.of(
                    date,
                    end,
                    offset
            );

            Appointment a = new Appointment(
                    appointmentId,
                    description,
                    location,
                    type,
                    startOdt,
                    endOdt,
                    clientId,
                    userId,
                    contactId
            );

            if (appointmentId == -1) {
                if (AppointmentValidator.isNotAppointmentConflict(
                        startOdt.toLocalDateTime(),
                        endOdt.toLocalDateTime(),
                        clientId,
                        appointmentDAO.getAll())
                ) {
                    appointmentDAO.save(a);
                } else {
                    String msg = "The client is already booked at that time";
                    new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                    return;
                }
            } else {
                if (AppointmentValidator.isNotAppointmentConflict(
                        startOdt.toLocalDateTime(),
                        endOdt.toLocalDateTime(),
                        clientId,
                        appointmentId,
                        appointmentDAO.getAll())
                ) {
                    appointmentDAO.update(appointmentId, a);

                } else {
                    String msg = "The client is already booked at that time";
                    new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                    return;
                }
            }

            clearInputs();
            stage.close();

        } else {
            String alertMsg = "All fields are required";
            new Alert(Alert.AlertType.ERROR, alertMsg).showAndWait();
        }
    }

    @FXML
    private void onCancel(ActionEvent e) {
        Button closeBtn = (Button) e.getSource();
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    public void setAppointmentViewLabelText(String s) {
        appointmentViewLabel.setText(s);
    }

    public void setAppointmentIdInputText(String s) {
        idInput.setText(s);
    }

    private void initComboBoxes() throws SQLException {
        ObservableList<LocalTime> businessHours = BusinessHoursGenerator.generateBusinessHours();

        userCombo.setItems(FXCollections.observableArrayList(users.values()));
        clientCombo.setItems(FXCollections.observableArrayList(clients.values()));
        contactCombo.setItems(FXCollections.observableArrayList(contacts.values()));
        startCombo.setItems(businessHours);
        endCombo.setItems(businessHours);

        Callback<ListView<User>, ListCell<User>> userCellFactory = ComboCellFactoryUtil.createCellFactory();
        Callback<ListView<Client>, ListCell<Client>> clientCellFactory = ComboCellFactoryUtil.createCellFactory();
        Callback<ListView<Contact>, ListCell<Contact>> contactCellFactory = ComboCellFactoryUtil.createCellFactory();

        userCombo.setCellFactory(userCellFactory);
        clientCombo.setCellFactory(clientCellFactory);
        contactCombo.setCellFactory(contactCellFactory);

        userCombo.setButtonCell(userCellFactory.call(null));
        contactCombo.setButtonCell(contactCellFactory.call(null));
        clientCombo.setButtonCell(clientCellFactory.call(null));
    }

    private void setTextFormatters() {
        descriptionInput.setTextFormatter(new TextFormatter<>(inputFilter));
        locationInput.setTextFormatter(new TextFormatter<>(inputFilter));
        typeInput.setTextFormatter(new TextFormatter<>(inputFilter));
    }

    private boolean validateInputs() {
        return !(descriptionInput.getText().isEmpty() ||
                locationInput.getText().isEmpty() ||
                typeInput.getText().isEmpty() ||
                userCombo.getSelectionModel().isEmpty() ||
                clientCombo.getSelectionModel().isEmpty() ||
                contactCombo.getSelectionModel().isEmpty() ||
                datePicker.getValue() == null ||
                startCombo.getSelectionModel().isEmpty() ||
                endCombo.getSelectionModel().isEmpty());
    }

    private void clearInputs() {
        descriptionInput.clear();
        locationInput.clear();
        typeInput.clear();
    }

    private void initModifyAppointment(Appointment appointment) {
        descriptionInput.setText(appointment.getDescription());
        locationInput.setText(appointment.getLocation());
        typeInput.setText(appointment.getType());
        userCombo.setValue(users.get(appointment.getUserId()));
        clientCombo.setValue(clients.get(appointment.getClientId()));
        contactCombo.setValue(contacts.get(appointment.getContactId()));
        datePicker.setValue(appointment.getStart().toLocalDate());
        startCombo.setValue(appointment.getStart().toLocalTime());
        endCombo.setValue(appointment.getEnd().toLocalTime());
    }

}
