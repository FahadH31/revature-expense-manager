package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

public class UserDao {
    public User findByCredentials(String username, String password, Connection conn)
            throws SQLException {
        String sql = """
                SELECT id, username, role, full_name
                FROM users
                WHERE username = ? AND password = ?
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role"),
                        resultSet.getString("full_name")
                );
            }
        }
    }
}
