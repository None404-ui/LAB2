package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // CREATE  добавление нового пользователя
    public static boolean addUser(String username, String email, String passwordHash) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, passwordHash);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    // READ  поиск пользователя по ID
    public static void getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found:");
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Created: " + rs.getTimestamp("created_at"));
            } else {
                System.out.println("User not found with ID: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
    }

    // READ  поиск пользователя по имени
    public static void getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found:");
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
            } else {
                System.out.println("User not found: " + username);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
    }

    // READ  получение всех пользователей
    public static void getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("All users:");
            while (rs.next()) {
                System.out.printf("ID: %d, Username: %s, Email: %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
    }

    // UPDATE  обновление email пользователя
    public static boolean updateUserEmail(int userId, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newEmail);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // DELETE  удаление пользователя
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}