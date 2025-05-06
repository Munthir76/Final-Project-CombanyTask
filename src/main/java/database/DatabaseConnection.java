package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB = "company_db";
    private static final String USER = "root";
    private static final String PASSWORD = "2340042";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {

            String url = String.format("jdbc:mysql://%s:%s/%s", HOST, PORT, DB);
            this.connection = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("Connected to database: " + url + " successful !! ");
        } catch (Exception ex) {
            System.out.println("Database Connection Failed: " + ex.getMessage());
            throw new SQLException(ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        // If the instance is not created or the connection is closed, create a new instance
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }

        return instance;
    }}


