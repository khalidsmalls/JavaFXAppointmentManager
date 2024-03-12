package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryDAO {

    private static CountryDAO instance;

    private ObservableList<Country> countries;

    private final Connection conn;

    public static CountryDAO getInstance() {
        if (instance == null) {
            instance = new CountryDAO();
        }
        return instance;
    }

    private CountryDAO() {
        this.conn = MainApplication.getConnection();
    }

    public ObservableList<Country> getAll() throws SQLException {
        if (countries == null) {
            countries = FXCollections.observableArrayList();
        } else {
            countries.clear();
        }

        String query = "SELECT country_id, name FROM countries";
        ResultSet resultSet;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("country_id");
                String name = resultSet.getString("name");

                countries.add(new Country(id, name));
            }
        }
        return countries;
    }

    public Country get(int id) throws SQLException {
        String query = "SELECT name FROM countries WHERE country_id=?";
        ResultSet resultSet;
        Country country = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                country = new Country(id, name);
            }
        }
        return country;
    }
}
