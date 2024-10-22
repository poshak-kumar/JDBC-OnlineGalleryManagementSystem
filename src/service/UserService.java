package service;

import db.DatabaseUtil;
import model.User;

import java.sql.*;

public class UserService {
    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // Ideally, hash this password
            pstmt.executeUpdate();
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Ideally, hash this password
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If user exists
        }
    }
}
