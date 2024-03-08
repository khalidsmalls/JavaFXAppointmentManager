package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.AdministrativeDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DivisionDAO {

    private static DivisionDAO instance;

    private ObservableList<AdministrativeDivision> divisions;

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

    public ObservableList<AdministrativeDivision> getAll() throws SQLException {
        if (divisions == null) {
            divisions = FXCollections.observableArrayList();
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

                divisions.add(
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
}
