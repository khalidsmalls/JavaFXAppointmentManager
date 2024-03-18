package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.DAO.ClientDAO;
import com.smalls.javafxappointmentmanager.DAO.CountryDAO;
import com.smalls.javafxappointmentmanager.DAO.DivisionDAO;
import com.smalls.javafxappointmentmanager.model.AdministrativeDivision;
import com.smalls.javafxappointmentmanager.model.Client;
import com.smalls.javafxappointmentmanager.model.Country;
import com.smalls.javafxappointmentmanager.utils.ComboCellFactoryUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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

    private ObservableMap<Integer, Country> countries;

    private ObservableMap<Integer, AdministrativeDivision> divisions;

    private Client client;

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

    public ClientViewController() {}

    public ClientViewController(Client client) {
        this.client = client;
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
            String msg = "There was an error fetching data: ";
            new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            throw new RuntimeException(e);
        }

        if (client != null) {
            try {
                initModifyClient(client);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        setTextFormatters();
    }

    @FXML
    private void onSave(ActionEvent e) throws SQLException {
        String name, address, postalCode, phone;
        int id, divisionId;
        ClientDAO clientDAO = ClientDAO.getInstance();
        Scene currentScene = ((Button) e.getSource()).getScene();
        Stage stage = (Stage) currentScene.getWindow();

        if (validateInputs()) {
            name = clientNameInput.getText().trim();
            address = clientAddressInput.getText().trim();
            postalCode = clientPostalCodeInput.getText().trim();
            phone = clientPhoneInput.getText().trim();
            divisionId = divisionCombo.getValue().getId();
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
                clientDAO.update(id, c);
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


    private void initComboBoxes() throws SQLException {
        countryCombo.setItems(FXCollections.observableArrayList(countries.values()));
        Callback<ListView<Country>, ListCell<Country>> countryCellFactory = ComboCellFactoryUtil.createCellFactory();
        Callback<ListView<AdministrativeDivision>, ListCell<AdministrativeDivision>> divisionCellFactory = ComboCellFactoryUtil.createCellFactory();

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

    private boolean validateInputs() {
        return !(clientNameInput.getText().isEmpty() ||
                clientAddressInput.getText().isEmpty() ||
                clientPostalCodeInput.getText().isEmpty() ||
                clientPhoneInput.getText().isEmpty() ||
                countryCombo.getSelectionModel().isEmpty() ||
                divisionCombo.getSelectionModel().isEmpty());
    }

    public void setClientViewLabelText(String s) {
        clientViewLabel.setText(s);
    }

    public void setClientIdInputText(String s) {
        clientIdInput.setText(s);
    }

    private void initModifyClient(Client client) throws SQLException {
        clientNameInput.setText(client.getName());
        clientAddressInput.setText(client.getAddress());
        clientPostalCodeInput.setText(client.getPostalCode());
        clientPhoneInput.setText(client.getPhone());

        int divisionId = client.getDivisionId();
        int countryId = divisions.get(divisionId).getCountryId();

        countryCombo.setValue(countries.get(countryId));
        divisionCombo.setValue(divisions.get(divisionId));
    }

    private void setTextFormatters() {
        clientNameInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientAddressInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientPostalCodeInput.setTextFormatter(new TextFormatter<>(inputFilter));
        clientPhoneInput.setTextFormatter(new TextFormatter<>(inputFilter));
    }

    private void clearInputs() {
        clientIdInput.clear();
        clientNameInput.clear();
        clientAddressInput.clear();
        clientPostalCodeInput.clear();
        clientPhoneInput.clear();
    }
}


