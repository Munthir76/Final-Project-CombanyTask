package database;

import Io.User;
import java.sql.*;

public class UserController {
    private Connection connection;

    public UserController() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public synchronized User validateLogin(String userId, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }
        return null;
    }
}




