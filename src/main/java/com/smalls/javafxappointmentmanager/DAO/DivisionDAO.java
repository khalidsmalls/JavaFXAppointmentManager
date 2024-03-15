package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.AdministrativeDivision;
import com.smalls.javafxappointmentmanager.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DivisionDAO {

    private static DivisionDAO instance;

    private ObservableMap<Integer, AdministrativeDivision> divisions;

    private final Connection conn;

    public static DivisionDAO getInstance() {
        if (instance == null) {
            instance = new DivisionDAO();
        }
        return instance;
    }

    private DivisionDAO() {
        this.conn = MainApplication.getConnection();
    }

    public ObservableMap<Integer, AdministrativeDivision> getAll() throws SQLException {
        if (divisions == null) {
            divisions = FXCollections.observableHashMap();
        } else {
            divisions.clear();
        }

        String query = "SELECT division_id, " +
                "name, " +
                "country_id " +
                "FROM administrative_divisions";
        ResultSet resultSet;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("division_id");
                String name = resultSet.getString("name");
                int countryId = resultSet.getInt("country_id");

                divisions.put(
                        id,
                        new AdministrativeDivision(
                                id,
                                name,
                                countryId
                        )
                );
            }
        }
        return divisions;
    }

    public AdministrativeDivision get(int id) throws SQLException {
        String query = "SELECT name, country_id FROM administrative_divisions WHERE division_id=?";
        ResultSet resultSet;
        AdministrativeDivision division = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int country_id = resultSet.getInt("country_id");
                division = new AdministrativeDivision(id, name, country_id);
            }
        }
        return division;
    }
}
