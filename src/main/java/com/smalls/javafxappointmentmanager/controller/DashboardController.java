package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.*;
import com.smalls.javafxappointmentmanager.model.*;
import com.smalls.javafxappointmentmanager.utils.CustomLogger;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;


public class DashboardController implements Initializable {

    @FXML
    private TextField clientSearchInput;

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, Integer> clientIdCol;

    @FXML
    private TableColumn<Client, String> clientNameCol;

    @FXML
    private TableColumn<Client, String> clientAddressCol;

    @FXML
    private TableColumn<Client, String> clientPostalCodeCol;

    @FXML
    private TableColumn<Client, String> clientPhoneCol;

    @FXML
    private TableColumn<Client, Integer> clientDivisionCol;

    @FXML
    private TableColumn<Client, Integer> clientCountryCol;

    @FXML
    private TableView<Appointment> apptTable;

    @FXML
    private TableColumn<Appointment, Integer> apptIdCol;

    @FXML
    private TableColumn<Appointment, String> apptDescriptionCol;

    @FXML
    private TableColumn<Appointment, String> apptLocationCol;

    @FXML
    private TableColumn<Appointment, String> apptTypeCol;

    @FXML
    private TableColumn<Appointment, OffsetDateTime> apptDateCol;

    @FXML
    private TableColumn<Appointment, OffsetDateTime> apptStartCol;

    @FXML
    private TableColumn<Appointment, OffsetDateTime> apptEndCol;

    @FXML
    private TableColumn<Appointment, Integer> apptClientCol;

    @FXML
    private TableColumn<Appointment, Integer> apptContactCol;

    @FXML
    private TableColumn<Appointment, Integer> apptUserCol;

    private ObservableList<Client> clientObservableList;

    private ObservableList<Appointment> appointmentObservableList;

    private ObservableMap<Integer, Client> clients;

    private final User user;

    private Stage stage;

    private ClientDAO clientDAO;

    private AppointmentDAO appointmentDAO;

    private final String resourcePath = "/com/smalls/javafxappointmentmanager/view/";

    private final URL stylesheet = getClass()
            .getResource("/com/smalls/javafxappointmentmanager/styles.css");

    //limit text input length to 50 characters
    private final UnaryOperator<TextFormatter.Change> lengthFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 50) {
            return null;
        }
        return change;
    };

    private final Comparator<Client> compareClientsById = Comparator
            .comparingInt(Client::getId);

    private final Comparator<Appointment> compareAppointmentsById = Comparator
            .comparingInt(Appointment::getId);

    private final MapChangeListener<Integer, Client> clientMapChangeListener = change -> {
        if (change.wasAdded()) {
            Client update = change.getValueAdded();
            clientObservableList.sort(compareClientsById);
            int index = Collections.binarySearch(
                    clientObservableList,
                    update,
                    compareClientsById
            );
            if (index >= 0) {
                clientObservableList.set(index, update);
                return;
            }
            clientObservableList.add(update);
        } else {
            clientObservableList.remove(change.getValueRemoved());
        }

    };

    private final MapChangeListener<Integer, Appointment> apptMapChangeListener = change -> {
        if (change.wasAdded()) {
            Appointment update = change.getValueAdded();
            appointmentObservableList.sort(compareAppointmentsById);
            int index = Collections.binarySearch(
                    appointmentObservableList,
                    update,
                    compareAppointmentsById
            );
            if (index >= 0) {
                appointmentObservableList.set(index, update);
                return;
            }
            appointmentObservableList.add(update);
        } else {
            appointmentObservableList.remove(change.getValueRemoved());
        }
    };

    public DashboardController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientSearchInput.setTextFormatter(new TextFormatter<>(lengthFilter));
        clientDAO = ClientDAO.getInstance();
        appointmentDAO = AppointmentDAO.getInstance();
        stage = new Stage();

        try {
            clients = clientDAO.getAll();
            ObservableMap<Integer, Appointment> appointments = appointmentDAO.getAll();
            clientObservableList = FXCollections.observableArrayList(clients.values());
            appointmentObservableList = FXCollections.observableArrayList(appointments.values());
            clients.addListener(clientMapChangeListener);
            appointments.addListener(apptMapChangeListener);
            initClientTable();
            initApptTable();
        } catch (SQLException | RuntimeException e) {
            String alertMsg = "There was an error loading the dashboard data";
            String logMsg = "error loading dashboard data: " + e.getMessage();

            new Alert(Alert.AlertType.ERROR, alertMsg).showAndWait();
            CustomLogger.log(getClass().getName(), Level.SEVERE, logMsg);
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void onClientSearch() {
        String searchString = clientSearchInput.getText().trim();
        ObservableList<Client> searchResult = FXCollections.observableArrayList();

        //if search string is empty, show all clients
        if (searchString.isEmpty()) {
            clientTable.setItems(clientObservableList);
            clientTable.setPlaceholder(new Text("There are currently no clients"));
        } else {
            clientTable.setItems(searchResult);
            clientTable.setPlaceholder(new Text("Client not found"));
            try {
                //try to get the client by id
                int id = Integer.parseInt(searchString);
                Client c = clients.get(id);
                if (c != null) {
                    searchResult.add(c);
                }
                if (!searchResult.isEmpty()) {
                    clientTable.getSelectionModel().select(c);
                }
            } catch (NumberFormatException e) {
                //search string wasn't a number, so search by name
                for (Client c : clients.values()) {
                    if (c.getName().toLowerCase().contains(searchString.toLowerCase())) {
                        searchResult.add(c);
                    }
                }
            }
        }

    }

    @FXML
    private void onNewClient() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL location =  getClass().getResource(
                resourcePath + "client-view.fxml"
        );
        loader.setLocation(location);
        Parent root = loader.load();
        ClientViewController controller = loader.getController();
        controller.setClientViewLabelText("New Client");
        controller.setClientIdInputText("Auto Gen - Disabled");
        Scene scene = new Scene(root);

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toString());
        }
        stage.setTitle("New Client");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(600);
        stage.show();
    }

    @FXML
    private void onModifyClient() throws IOException {
        Client client = clientTable.getSelectionModel().getSelectedItem();
        if (client == null) {
            String msg = "Please select a client";
            new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource(
                resourcePath + "client-view.fxml"
        );
        loader.setLocation(location);
        loader.setControllerFactory(param -> new ClientViewController(client));
        Parent root = loader.load();
        ClientViewController controller = loader.getController();
        controller.setClientViewLabelText("Modify Client");
        controller.setClientIdInputText(String.valueOf(client.getId()));
        Scene scene = new Scene(root);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toString());
        }
        stage.setTitle("Modify Client");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(600);
        stage.show();
    }

    @FXML
    private void onDeleteClient() throws SQLException {
        Client client = clientTable.getSelectionModel().getSelectedItem();
        String alertMsg = "Are you sure you would like to delete " +
                client.getName() + " and his/her scheduled appointments?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, alertMsg);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            appointmentDAO.deleteByClientId(client.getId());
            clientDAO.delete(client);
        }
    }

    @FXML
    private void onNewAppt() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL location =  getClass().getResource(
                resourcePath + "appointment-view.fxml"
        );
        loader.setLocation(location);
        Parent root = loader.load();
        AppointmentViewController controller = loader.getController();
        controller.setAppointmentViewLabelText("New Appointment");
        controller.setAppointmentIdInputText("Auto Gen - Disabled");
        Scene scene = new Scene(root);

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toString());
        }
        stage.setTitle("New Appointment");
        stage.setScene(scene);
        stage.setMinWidth(1150);
        stage.setMinHeight(600);
        stage.show();
    }

    @FXML
    private void onModifyAppt() throws IOException {
        Appointment appointment = apptTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            String msg = "Please select an appointment";
            new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource(
                resourcePath + "appointment-view.fxml"
        );
        loader.setLocation(location);
        loader.setControllerFactory(param -> new AppointmentViewController(appointment));
        Parent root = loader.load();
        AppointmentViewController controller = loader.getController();
        controller.setAppointmentViewLabelText("Modify Appointment");
        controller.setAppointmentIdInputText(String.valueOf(appointment.getId()));
        Scene scene = new Scene(root);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toString());
        }
        stage.setTitle("Modify Appointment");
        stage.setScene(scene);
        stage.setMinWidth(1150);
        stage.setMinHeight(600);
        stage.show();
    }

    @FXML
    private void onDeleteAppt() throws SQLException {
        Appointment appointment = apptTable.getSelectionModel().getSelectedItem();
        String alertMsg = "Are you sure you would like to delete the selected appointment: " +
                appointment.getDescription() + "?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, alertMsg);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            appointmentDAO.delete(appointment.getId());
        }
    }

    @FXML
    private void onViewReports() {
    }

    @FXML
    private void onClose(ActionEvent e) {
        String msg = "Are you sure you would like to close the application?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Scene currentScene = ((Button) e.getSource()).getScene();
            Stage currentStage = (Stage) currentScene.getWindow();
            currentStage.close();
        }
    }

    private void initClientTable() {
        clientTable.setItems(clientObservableList);
        clientTable.setPlaceholder(new Text("there are currently no clients"));
        setClientTableCellValueFactories();
        setClientTableCellFactories();
    }

    private void setClientTableCellValueFactories() {
        clientIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        clientPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        clientDivisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
        clientCountryCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
    }

    private void setClientTableCellFactories() {
        ObservableMap<Integer, Country> countries = FXCollections.observableHashMap();
        ObservableMap<Integer, AdministrativeDivision> divisions = FXCollections.observableHashMap();
        DivisionDAO divisionDAO = DivisionDAO.getInstance();
        CountryDAO countryDAO = CountryDAO.getInstance();

        clientDivisionCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer divisionId, boolean empty) {
                super.updateItem(divisionId, empty);
                if (empty || divisionId == null) {
                    setText(null);
                } else {
                    AdministrativeDivision division = null;
                    try {
                        if (divisions.get(divisionId) == null) {
                            division = divisionDAO.get(divisionId);
                            divisions.put(divisionId, division);
                        } else {
                            division = divisions.get(divisionId);
                        }
                        setText(division.getName());
                    } catch (SQLException e) {
                        setText("");
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        clientCountryCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer divisionId, boolean empty) {
                super.updateItem(divisionId, empty);
                if (empty || divisionId == null) {
                    setText(null);
                } else {
                    Country country = null;
                    try {
                        int countryId = divisionDAO.get(divisionId).getCountryId();
                        if (countries.get(countryId) == null) {
                            country = countryDAO.get(countryId);
                            countries.put(countryId, country);
                        } else {
                            country = countries.get(countryId);
                        }
                        setText(country.getName());
                    } catch (SQLException e) {
                        setText("");
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void initApptTable() {
        apptTable.setItems(appointmentObservableList);
        apptTable.setPlaceholder(new Text("there are currently no appointments"));
        setApptTableCellValueFactories();
        setApptTableCellFactories();
    }

    private void setApptTableCellValueFactories() {
        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptDateCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptClientCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        apptContactCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        apptUserCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    private void setApptTableCellFactories() {
        ContactDAO contactDAO = ContactDAO.getInstance();
        UserDAO userDAO = UserDAO.getInstance();
        ObservableMap<Integer, Contact> contacts = FXCollections.observableHashMap();
        ObservableMap<Integer, User> users = FXCollections.observableHashMap();

        apptDateCol.setCellFactory(c -> offsetDateTimeCellFactory("yyyy-MM-dd"));
        apptStartCol.setCellFactory(c -> offsetDateTimeCellFactory("hh:mm a"));
        apptEndCol.setCellFactory(c -> offsetDateTimeCellFactory("hh:mm a"));

        apptClientCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer clientId, boolean empty) {
                super.updateItem(clientId, empty);
                if (empty || clientId == null) {
                    setText(null);
                } else {
                    Client client = clients.get(clientId);
                    setText(client == null ? "Unknown" : client.getName());
                }
            }
        });

        apptContactCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer contactId, boolean empty) {
                super.updateItem(contactId, empty);
                if (empty || contactId == null) {
                    setText(null);
                } else {
                    Contact contact;
                    if (contacts.get(contactId) != null) {
                        contact = contacts.get(contactId);
                    } else {
                        try {
                            contact = contactDAO.get(contactId);
                            contacts.put(contactId, contact);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    setText(contact == null ? "Unknown" : contact.getName());
                }
            }
        });

        apptUserCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer userId, boolean empty) {
                super.updateItem(userId, empty);
                if (empty || userId == null) {
                    setText(null);
                } else {
                    User user;
                    if (users.get(userId) != null) {
                        user = users.get(userId);
                    } else {
                        try {
                            user = userDAO.get(userId);
                            users.put(userId, user);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    setText(user == null ? "Unknown" : user.getName());
                }
            }
        });
    }

    //cell factory for start and end columns
    private <S> TableCell<S, OffsetDateTime> offsetDateTimeCellFactory(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return new TableCell<>() {
            @Override
            protected void updateItem(OffsetDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(formatter.format(time));
                }
            }
        };

    }

}
