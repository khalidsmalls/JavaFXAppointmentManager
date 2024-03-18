package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.*;
import java.time.*;

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
                "appointment_type, " +
                "start_time, " +
                "end_time, " +
                "client_id, " +
                "user_id, " +
                "contact_id " +
                "FROM appointments";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("appointment_id");

                ZonedDateTime start = resultSet.getTimestamp("start_time")
                        .toInstant()
                        .atZone(ZoneId.systemDefault());
                ZonedDateTime end = resultSet.getTimestamp("end_time")
                        .toInstant()
                        .atZone(ZoneId.systemDefault());

                appointments.put(
                        id,
                        new Appointment(
                                id,
                                resultSet.getString("description"),
                                resultSet.getString("location"),
                                resultSet.getString("appointment_type"),
                                start.toOffsetDateTime(),
                                end.toOffsetDateTime(),
                                resultSet.getInt("client_id"),
                                resultSet.getInt("user_id"),
                                resultSet.getInt("contact_id")
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
        OffsetDateTime start = appointment.getStart();
        OffsetDateTime end = appointment.getEnd();
        int clientId = appointment.getClientId();
        int userId = appointment.getUserId();
        int contactId = appointment.getContactId();
        int nextId;

        String update = "INSERT INTO appointments " +
                "(description, location, appointment_type, start_time, end_time, client_id, user_id, contact_id) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(
                update,
                Statement.RETURN_GENERATED_KEYS)
        ) {
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

    public void update(int id, Appointment appointment) throws SQLException {
        String update = "UPDATE appointments " +
                "SET description=?, " +
                "location=?, " +
                "appointment_type=?, " +
                "start_time=?, " +
                "end_time=?, " +
                "client_id=?, " +
                "user_id=?, " +
                "contact_id=? " +
                "WHERE appointment_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            Timestamp start = Timestamp.valueOf(appointment.getStart().toLocalDateTime());
            Timestamp end = Timestamp.valueOf(appointment.getEnd().toLocalDateTime());

            stmt.setString(1, appointment.getDescription());
            stmt.setString(2, appointment.getLocation());
            stmt.setString(3, appointment.getType());
            stmt.setTimestamp(4, start);
            stmt.setTimestamp(5, end);
            stmt.setInt(6, appointment.getClientId());
            stmt.setInt(7, appointment.getUserId());
            stmt.setInt(8, appointment.getContactId());
            stmt.setInt(9, id);

            if (stmt.executeUpdate() == 1) appointments.put(id, appointment);
        }
    }

    public void delete(int id) throws SQLException {
        String update = "DELETE FROM appointments WHERE appointment_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 1) appointments.remove(id);
        }
    }

    public void deleteByClientId(int clientId) throws SQLException {
        String update = "DELETE FROM appointments WHERE client_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setInt(1, clientId);
            if (stmt.executeUpdate() > 0) {
                appointments.entrySet().removeIf(
                        entry -> entry.getValue().getClientId() == clientId
                );
            }
        }
    }
}
