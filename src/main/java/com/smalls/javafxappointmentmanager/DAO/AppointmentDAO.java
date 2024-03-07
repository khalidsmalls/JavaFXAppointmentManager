package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AppointmentDAO {

    private static AppointmentDAO instance;

    private ObservableMap<Integer, Appointment> appointments;

    private final Connection conn;

    public static AppointmentDAO getInstance() {
        if (instance == null) {
            instance = new AppointmentDAO();
        }
        return instance;
    }

    private AppointmentDAO() {
        this.conn = MainApplication.getConnection();
    }

    public ObservableMap<Integer, Appointment> getAll() throws SQLException {
        if (appointments == null) {
            appointments = FXCollections.observableHashMap();
        } else {
            appointments.clear();
        }
        ResultSet resultSet;
        String query = "SELECT appointment_id, " +
                "description, " +
                "location, " +
                "type, " +
                "start, " +
                "end, " +
                "clientId, " +
                "userId, " +
                "contactId " +
                "FROM appointments";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("appointment_id");
                appointments.put(
                        id,
                        new Appointment(
                                id,
                                resultSet.getString("description"),
                                resultSet.getString("location"),
                                resultSet.getString("type"),
                                resultSet.getTimestamp("start")
                                        .toLocalDateTime()
                                        .atZone(ZoneId.systemDefault()),
                                resultSet.getTimestamp("end")
                                        .toLocalDateTime()
                                        .atZone(ZoneId.systemDefault()),
                                resultSet.getInt("clientId"),
                                resultSet.getInt("userId"),
                                resultSet.getInt("contactId")
                        )

                );
            }
        }

        return appointments;
    }

    public void save(Appointment appointment) throws SQLException {
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String type = appointment.getType();
        ZonedDateTime start = appointment.getStart();
        ZonedDateTime end = appointment.getEnd();
        int clientId = appointment.getClientId();
        int userId = appointment.getUserId();
        int contactId = appointment.getContactId();
        int nextId;

        String update = "INSERT INTO appointments " +
                "(description, location, type, start, end, clientId, userId, contactId) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(update, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, description);
            stmt.setString(2, location);
            stmt.setString(3, type);
            stmt.setTimestamp(4, Timestamp.from(start.toInstant()));
            stmt.setTimestamp(5, Timestamp.from(end.toInstant()));
            stmt.setInt(6, clientId);
            stmt.setInt(7, userId);
            stmt.setInt(8, contactId);

            if (stmt.executeUpdate() == 1) {
                ResultSet resultSet = stmt.getGeneratedKeys();
                resultSet.next();
                nextId = resultSet.getInt(1);

                appointments.put(
                        nextId,
                        new Appointment(
                                nextId,
                                description,
                                location,
                                type,
                                start,
                                end,
                                clientId,
                                userId,
                                contactId
                        )
                );
            }
        }
    }
}
