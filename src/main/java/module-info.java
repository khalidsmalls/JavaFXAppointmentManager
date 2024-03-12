module com.smalls.javafxappointmentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.smalls.javafxappointmentmanager to javafx.fxml;
    exports com.smalls.javafxappointmentmanager;
    exports com.smalls.javafxappointmentmanager.controller;
    exports com.smalls.javafxappointmentmanager.model;
    opens com.smalls.javafxappointmentmanager.controller to javafx.fxml;
}