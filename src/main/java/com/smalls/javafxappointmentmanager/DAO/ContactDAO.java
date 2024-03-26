package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.Contact;
import com.smalls.javafxappointmentmanager.model.ContactReportRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDAO {

    private static ContactDAO instance;

    private ObservableMap<Integer, Contact> contacts;

    private final Connection conn;

    public static ContactDAO getInstance() {
        if (instance == null) {
            instance = new ContactDAO();
        }
        return instance;
    }

    private ContactDAO() {
        this.conn = MainApplication.getConnection();
    }

    public ObservableMap<Integer, Contact> getAll() throws SQLException {
        if (contacts == null) {
            contacts = FXCollections.observableHashMap();
        } else {
            contacts.clear();
        }

        String query = "SELECT contact_id, name FROM contacts";
        ResultSet resultSet;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("contact_id");
                String name = resultSet.getString("name");

                contacts.put(id, new Contact(id, name));
            }
        }
        return contacts;
    }

    public Contact get(int id) throws SQLException {
        String query = "SELECT name FROM contacts WHERE contact_id=?";
        ResultSet resultSet;
        Contact contact = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                contact = new Contact(id, name);
            }
        }
        return contact;
    }

    public ObservableMap<Integer, ContactReportRecord> getContactReportRecords() throws SQLException {
        ObservableMap<Integer, ContactReportRecord> records = FXCollections.observableHashMap();
        String query = "SELECT contact_id, " +
                "contact, " +
                "num_appointments, " +
                "total_appointment_hrs " +
                "FROM contact_report";
        ResultSet resultSet;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int contactId = resultSet.getInt("contact_id");
                records.put(
                        contactId,
                        new ContactReportRecord(
                                contactId,
                                resultSet.getString("contact"),
                                resultSet.getInt("num_appointments"),
                                resultSet.getDouble("total_appointment_hrs")
                        )
                );
            }
        }

        return records;
    }
}
