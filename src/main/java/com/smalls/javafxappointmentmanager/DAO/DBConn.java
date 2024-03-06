package com.smalls.javafxappointmentmanager.DAO;

import java.sql.*;

public class DBConn {
    private static String protocol;

    private static String vendor;

    private static String location;

    private static String database;

    private static String username;

    private static String password;

    public static Connection conn;

    private static final String jdbcUrl =
            protocol +
            vendor +
            location +
            database +
            "?connectionTimeZone = SERVER";

    public static void openConnection() {
        try {
            conn = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            //log this
        }
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            //find more specific exception and probably log this
        }
    }

}
