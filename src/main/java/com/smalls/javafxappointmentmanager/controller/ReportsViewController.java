package com.smalls.javafxappointmentmanager.controller;

import com.smalls.javafxappointmentmanager.model.AppointmentReportRecord;
import com.smalls.javafxappointmentmanager.model.ContactReportRecord;
import com.smalls.javafxappointmentmanager.utils.CellFactoryUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ResourceBundle;

public class ReportsViewController implements Initializable {

    @FXML
    private TableView<AppointmentReportRecord> appointmentReport;

    @FXML
    private TableView<ContactReportRecord> contactReport;

    @FXML
    private TableColumn<AppointmentReportRecord, Integer> appointmentIdCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> descriptionCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> locationCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> typeCol;

    @FXML
    private TableColumn<AppointmentReportRecord, OffsetDateTime> dateCol;

    @FXML
    private TableColumn<AppointmentReportRecord, OffsetDateTime> startCol;

    @FXML
    private TableColumn<AppointmentReportRecord, OffsetDateTime> endCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> clientCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> addressCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> postalCodeCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> divisionCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> countryCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> phoneCol;

    @FXML
    private TableColumn<AppointmentReportRecord, String> contactCol;

    @FXML
    private TableColumn<ContactReportRecord, Integer> contactIdCol;

    @FXML
    private TableColumn<ContactReportRecord, String> contactNameCol;

    @FXML
    private TableColumn<ContactReportRecord, Integer> numAppointmentsCol;

    @FXML
    private TableColumn<ContactReportRecord, Double> totalHrsCol;

    private final ObservableMap<Integer, AppointmentReportRecord> appointmentRecords;

    private final ObservableMap<Integer, ContactReportRecord> contactReportRecords;

    public ReportsViewController(
            ObservableMap<Integer, AppointmentReportRecord> appointmentRecords,
            ObservableMap<Integer, ContactReportRecord> contactReportRecords
    ) {
       this.appointmentRecords = appointmentRecords;
       this.contactReportRecords = contactReportRecords;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAppointmentReport();
        initContactReport();
    }

    @FXML
    private void onClose(ActionEvent e) {
        Scene currentScene = ((Button) e.getSource()).getScene();
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.close();
    }

    private void initAppointmentReport() {
        appointmentReport.setItems(FXCollections.observableArrayList(appointmentRecords.values()));
        appointmentReport.setPlaceholder(new Text("there are currently no appointments"));
        setApptTableCellValueFactories();
        setApptTableCellFactories();
    }

    private void setApptTableCellValueFactories() {
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    private void setApptTableCellFactories() {
        dateCol.setCellFactory(c -> CellFactoryUtils.offsetDateTimeTableCellFactory("yyyy-MM-dd"));
        startCol.setCellFactory(c -> CellFactoryUtils.offsetDateTimeTableCellFactory("hh:mm a"));
        endCol.setCellFactory(c -> CellFactoryUtils.offsetDateTimeTableCellFactory("hh:mm a"));
    }

    private void initContactReport() {
        contactReport.setItems(FXCollections.observableArrayList(contactReportRecords.values()));
        contactReport.setPlaceholder(new Text("there is no report data"));

        contactIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        numAppointmentsCol.setCellValueFactory(new PropertyValueFactory<>("numAppointments"));
        totalHrsCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointmentHrs"));
    }
}
