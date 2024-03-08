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
                "clientId, " +
                "userId, " +
                "contactId " +
                "FROM appointments";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("appointment_id");

                //convert UTC timestamp to localDateTime object
                LocalDateTime startLocal = resultSet.getTimestamp("start_time")
                        .toLocalDateTime();
                LocalDateTime endLocal = resultSet.getTimestamp("end_time")
                        .toLocalDateTime();

                //convert UTC to system local timezone
                ZonedDateTime startTz = startLocal.atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime endTz = endLocal.atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault());

                appointments.put(
                        id,
                        new Appointment(
                                id,
                                resultSet.getString("description"),
                                resultSet.getString("location"),
                                resultSet.getString("type"),
                                startTz.toOffsetDateTime(),
                                endTz.toOffsetDateTime(),
                                resultSet.getInt("clientId"),
                                resultSet.getInt("userId"),
                                resultSet.getInt("contactId")
                        )

                );
            }
        }

        return appointments;
    }

    public void save(Appointment a) throws SQLException {
        String description = a.getDescription();
        String location = a.getLocation();
        String type = a.getType();
        OffsetDateTime start = a.getStart();
        OffsetDateTime end = a.getEnd();
        int clientId = a.getClientId();
        int userId = a.getUserId();
        int contactId = a.getContactId();
        int nextId;

        String update = "INSERT INTO appointments " +
                "(description, location, type, start, end, clientId, userId, contactId) " +
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

    public void update(int id, Appointment a) throws SQLException {
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
            Timestamp start = Timestamp.valueOf(a.getStart().toLocalDateTime());
            Timestamp end = Timestamp.valueOf(a.getEnd().toLocalDateTime());

            stmt.setString(1, a.getDescription());
            stmt.setString(2, a.getLocation());
            stmt.setString(3, a.getType());
            stmt.setTimestamp(4, start);
            stmt.setTimestamp(5, end);
            stmt.setInt(6, a.getClientId());
            stmt.setInt(7, a.getUserId());
            stmt.setInt(8, a.getContactId());
            stmt.setInt(9, id);

            if (stmt.executeUpdate() == 1) appointments.put(id, a);
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
