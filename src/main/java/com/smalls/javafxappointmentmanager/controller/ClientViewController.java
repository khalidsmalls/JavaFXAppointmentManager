package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.ClientDAO;
import com.smalls.javafxappointmentmanager.DAO.CountryDAO;
import com.smalls.javafxappointmentmanager.DAO.DivisionDAO;
import com.smalls.javafxappointmentmanager.model.AdministrativeDivision;
import com.smalls.javafxappointmentmanager.model.Client;
import com.smalls.javafxappointmentmanager.model.Country;
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
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ClientViewController implements Initializable {

    @FXML
    private Label clientViewLabel;

    @FXML
    private TextField clientIdInput;

    @FXML
    private TextField clientNameInput;

    @FXML
    private TextField clientAddressInput;

    @FXML
    private TextField clientPostalCodeInput;

    @FXML
    private TextField clientPhoneInput;

    @FXML
    private ComboBox<Country> countryCombo;

    @FXML
    private ComboBox<AdministrativeDivision> divisionCombo;

    private Client client;

    private ObservableMap<Integer, Country> countries;

    private ObservableMap<Integer, AdministrativeDivision> divisions;

    private String clientViewLabelText;

    private int clientDivisionId;

    private final UnaryOperator<TextFormatter.Change> inputFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 50) {
            return null;
        } else if (newText.matches("([0-9a-zA-Z\\- ']*)")) {
            return change;
        } else {
            return null;
        }
    };

    public ClientViewController() {
    }

    public ClientViewController(Client c) {
        this.client = c;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientIdInput.setEditable(false);
        DivisionDAO divisionDAO = DivisionDAO.getInstance();

        try {
            countries = CountryDAO.getInstance().getAll();
            divisions = divisionDAO.getAll();
            initComboBoxes();
        } catch (SQLException e) {
            //TODO alert user
            throw new RuntimeException(e);
        }

        if (client != null) {
            try {
                initModifyClient(client);
            } catch (SQLException e) {
                //TODO handle exception
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    private void onSave(ActionEvent e) throws SQLException {
        String name, address, postalCode, phone;
        int id, divisionId;
        ClientDAO clientDAO = ClientDAO.getInstance();

        if (validateInputs()) {
            name = clientNameInput.getText().trim();
            address = clientAddressInput.getText().trim();
            postalCode = clientPostalCodeInput.getText().trim();
            phone = clientPhoneInput.getText().trim();
            divisionId = divisionCombo.getSelectionModel()
                    .getSelectedItem()
                    .getId();
        } else {
            new Alert(Alert.AlertType.ERROR, "All fields are required")
                    .showAndWait();
            return;
        }

        id = client != null ? client.getId() : -1;

        Client c = new Client(
                id,
                name,
                address,
                postalCode,
                phone,
                divisionId
        );

        if (id == -1) {
            clientDAO.save(c);
        } else {
            clientDAO.update(c.getId(), c);
        }
        ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();

    }



    @FXML
    private void onCancel(ActionEvent e) {
        ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
    }

    private void setTextFormatters() {
        clientNameInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientAddressInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientPostalCodeInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientPhoneInput.setTextFormatter(new TextFormatter<>(inputFilter));
    }

    private void initComboBoxes() throws SQLException {
        countryCombo.setItems(FXCollections.observableArrayList(countries.values()));
        countryCombo.setCellFactory(countryCellFactory);
        countryCombo.setButtonCell(countryCellFactory.call(null));
        divisionCombo.setCellFactory(divisionCellFactory);
        divisionCombo.setButtonCell(divisionCellFactory.call(null));

        countryCombo.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldValue, newValue) -> {
                    ObservableList<AdministrativeDivision> matchingDivs = divisions
                            .values()
                            .stream()
                            .filter(div -> div.getCountryId() == newValue.getId())
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
                    divisionCombo.setItems(matchingDivs);
                }));
    }

    private final Callback<ListView<Country>, ListCell<Country>> countryCellFactory = new Callback<>() {
        @Override
        public ListCell<Country> call(ListView<Country> countryListView) {
            return new ListCell<>() {
                @Override
                public void updateItem(Country c, boolean empty) {
                    super.updateItem(c, empty);
                    setText(empty ? null : c.getName());
                }
            };
        }
    };

    private final Callback<ListView<AdministrativeDivision>, ListCell<AdministrativeDivision>> divisionCellFactory = new Callback<>() {
        @Override
        public ListCell<AdministrativeDivision> call(ListView<AdministrativeDivision> administrativeDivisionListView) {
            return new ListCell<>() {
                @Override
                public void updateItem(AdministrativeDivision d, boolean empty) {
                    super.updateItem(d, empty);
                    setText(empty ? null : d.getName());
                }
            };
        }
    };

    private void clearInputs() {
        clientIdInput.clear();
        clientNameInput.clear();
        clientAddressInput.clear();
        clientPostalCodeInput.clear();
        clientPhoneInput.clear();
    }

    private boolean validateInputs() {
        return !(clientNameInput.getText().isEmpty() ||
                clientAddressInput.getText().isEmpty() ||
                clientPostalCodeInput.getText().isEmpty() ||
                clientPhoneInput.getText().isEmpty() ||
                countryCombo.getSelectionModel().isEmpty() ||
                divisionCombo.getSelectionModel().isEmpty());
    }

    public void setClientViewLabelText(String s) {
        this.clientViewLabelText = s;
    }

    public void setClientIdInputText(String s) {
        clientIdInput.setText(s);
    }


    private void initModifyClient(Client client) throws SQLException {
        clientViewLabel.setText("Modify Client");
        clientIdInput.setText(String.valueOf(client.getId()));
        clientNameInput.setText(client.getName());
        clientAddressInput.setText(client.getAddress());
        clientPostalCodeInput.setText(client.getPostalCode());
        clientPhoneInput.setText(client.getPhone());

        int divisionId = client.getDivisionId();
        int countryId = divisions.get(divisionId).getCountryId();

        countryCombo.setValue(countries.get(countryId));
        divisionCombo.setValue(divisions.get(divisionId));
    }
}


