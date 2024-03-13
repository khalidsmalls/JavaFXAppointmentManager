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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.OffsetDateTime;
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

    private ObservableMap<Integer, Appointment> appointments;

    private ClientDAO clientDAO;

    private AppointmentDAO appointmentDAO;

    private User user;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientSearchInput.setTextFormatter(new TextFormatter<>(lengthFilter));
        clientDAO = ClientDAO.getInstance();
        appointmentDAO = AppointmentDAO.getInstance();

        try {
            clients = clientDAO.getAll();
            appointments = appointmentDAO.getAll();
            clientObservableList = FXCollections.observableArrayList(clients.values());
            appointmentObservableList = FXCollections.observableArrayList(appointments.values());
            clients.addListener(clientMapChangeListener);
            appointments.addListener(apptMapChangeListener);
            initClientTable();
            initApptTable();
        } catch (SQLException | RuntimeException e) {
            new Alert(Alert.AlertType.ERROR,
                    "There was an error loading the dashboard data"
            ).showAndWait();
            CustomLogger.log(
                    getClass().getName(),
                    Level.SEVERE,
                    "error loading dashboard data: " + e.getMessage()
            );
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void onClientSearch() {
        
    }

    @FXML
    private void onNewClient() {
    }

    @FXML
    private void onModifyClient() {
    }

    @FXML
    private void onDeleteClient() {
    }

    @FXML
    private void onNewAppt() {
    }

    @FXML
    private void onModifyAppt() {
    }

    @FXML
    private void onDeleteAppt() {
    }

    @FXML
    private void onViewReports() {
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

        apptDateCol.setCellFactory(c -> dateTimeCellFactory("yyyy-MM-dd"));
        apptStartCol.setCellFactory(c -> dateTimeCellFactory("HH:mm a"));
        apptEndCol.setCellFactory(c -> dateTimeCellFactory("HH:mm a"));

        apptClientCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer clientId, boolean empty) {
                super.updateItem(clientId, empty);
                if (empty || clientId == null) {
                    setText(null);
                } else {
                    Client client = clients.get(clientId);
                    if (client != null) {
                        setText(client.getName());
                    } else {
                        setText("Unknown");
                    }
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

                    if (contact != null) {
                        setText(contact.getName());
                    } else {
                        setText("Unknown");
                    }
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

                    if (user != null) {
                        setText(user.getName());
                    } else {
                        setText("Unknown");
                    }
                }
            }
        });
    }

    //cell factory for start and end columns
    private <S> TableCell<S, OffsetDateTime> dateTimeCellFactory(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return new TableCell<>() {
            @Override
            protected void updateItem(OffsetDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(time.format(formatter));
                }
            }
        };

    }

    public void setUser(User user) {
        this.user = user;
    }

}
