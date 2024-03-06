package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO {

    private static ObservableMap<Integer, Client> CLIENTS;

    public static ObservableMap<Integer, Client> getAll() throws SQLException {
        CLIENTS = FXCollections.observableHashMap();
        ResultSet resultSet;
        String query = "SELECT " +
                "client_id, " +
                "name, " +
                "address, " +
                "postal_code, " +
                "phone, " +
                "division_id " +
                "FROM clients";

        try (PreparedStatement stmt = DBConn.conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("client_id");
                CLIENTS.put(
                        id,
                        new Client(
                                id,
                                resultSet.getString("name"),
                                resultSet.getString("address"),
                                resultSet.getString("postalCode"),
                                resultSet.getString("phone"),
                                resultSet.getInt("divisionId")
                        )
                );
            }
        }

        return CLIENTS;
    }

    public static void save(Client client) throws SQLException {
        String name = client.getName();
        String address = client.getAddress();
        String postalCode = client.getPostalCode();
        String phone = client.getPhone();
        int divisionId = client.getDivisionId();
        int nextId;

        String update = "INSERT INTO clients " +
                "(name, address, postal_code, phone, division_id) " +
                "VALUES (?,?,?,?,?)";

        try (PreparedStatement stmt = DBConn.conn.prepareStatement(update, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, postalCode);
            stmt.setString(4, phone);
            stmt.setInt(5, divisionId);

            if (stmt.executeUpdate() == 1) {
                ResultSet resultSet = stmt.getGeneratedKeys();
                resultSet.next();
                nextId = resultSet.getInt(1);

                CLIENTS.put(
                        nextId,
                        new Client(
                                nextId,
                                name,
                                address,
                                postalCode,
                                phone,
                                divisionId
                        )
                );
            }
        }
    }

    public static void update(int clientId, Client client) throws SQLException {
        String update = "UPDATE clients " +
                "SET name=?, address=?, postal_code=?, phone=?, division_id=? " +
                "WHERE client_id=?";

        try (PreparedStatement stmt = DBConn.conn.prepareStatement(update)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getAddress());
            stmt.setString(3, client.getPostalCode());
            stmt.setString(4, client.getPhone());
            stmt.setInt(5, client.getDivisionId());
            stmt.setInt(6, clientId);

            if (stmt.executeUpdate() == 1) {
                CLIENTS.put(clientId, client);
            }
        }
    }

    public static void delete(Client client) throws SQLException {
        String update = "DELETE FROM clients WHERE id=?";

        try (PreparedStatement stmt = DBConn.conn.prepareStatement(update)) {
            stmt.setInt(1, client.getId());

            if (stmt.executeUpdate() == 1) {
                CLIENTS.remove(client.getId());
            }
        }
    }
}
