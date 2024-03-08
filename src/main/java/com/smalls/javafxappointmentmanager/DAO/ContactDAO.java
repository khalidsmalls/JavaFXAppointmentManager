package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDAO {

    private static ContactDAO instance;

    private ObservableList<Contact> contacts;

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

    public ObservableList<Contact> getAll() throws SQLException {
        if (contacts == null) {
            contacts = FXCollections.observableArrayList();
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

                contacts.add(new Contact(id, name));
            }
        }
        return contacts;
    }
}
