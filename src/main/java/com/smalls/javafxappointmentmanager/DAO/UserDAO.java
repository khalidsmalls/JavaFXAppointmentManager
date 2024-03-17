package com.smalls.javafxappointmentmanager.DAO;

import com.smalls.javafxappointmentmanager.MainApplication;
import com.smalls.javafxappointmentmanager.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static UserDAO instance;

    private ObservableList<User> users;

    private final Connection conn;

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    private UserDAO() {
        this.conn = MainApplication.getConnection();
    }

    public ObservableList<User> getAll() throws SQLException {
        if (users == null) {
            users = FXCollections.observableArrayList();
        } else {
            users.clear();
        }

        String query = "SELECT user_id, username FROM users";
        ResultSet resultSet;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("username");

                users.add(new User(id, name));
            }
        }

        return users;
    }

    public User get(int id) throws SQLException {
        String query = "SELECT username FROM users WHERE user_id=?";
        ResultSet resultSet;
        User user = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("username");
                user = new User(id, name);
            }
        }
        return user;
    }

    public User authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username=? AND password=?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"), username);
            } else {
                return null;
            }
        }
    }

}
