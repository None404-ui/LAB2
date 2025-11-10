package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserRepositoryImp {
    // Инициализируем logger правильно
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImp.class);

    // CREATE добавление нового пользователя
    public static boolean addUser(String username, String email, String passwordHash) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        logger.info("Attempting to create user: username={}, email={}", username, email);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, passwordHash);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Successfully created user: {}", username);
            } else {
                logger.warn("Failed to create user: {}", username);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error adding user {}: {}", username, e.getMessage());
            return false;
        }
    }

    // READ  поиск пользователя по ID
    public static void getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        logger.debug("Searching for user by ID: {}", userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                logger.info("User found: ID={}, Username={}", userId, rs.getString("username"));
                System.out.println("User found:");
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Created: " + rs.getTimestamp("created_at"));
            } else {
                logger.debug("User not found with ID: {}", userId);
                System.out.println("User not found with ID: " + userId);
            }

        } catch (SQLException e) {
            logger.error("Error getting user by ID {}: {}", userId, e.getMessage());
            System.err.println("Error getting user: " + e.getMessage());
        }
    }

    // READ поиск пользователя по имени
    public static void getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        logger.debug("Searching for user by username: {}", username);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                logger.info("User found: {}", username);
                System.out.println("User found:");
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
            } else {
                logger.debug("User not found: {}", username);
                System.out.println("User not found: " + username);
            }

        } catch (SQLException e) {
            logger.error("Error getting user by username {}: {}", username, e.getMessage());
            System.err.println("Error getting user: " + e.getMessage());
        }
    }

    // READ получение всех пользователей
    public static void getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        logger.debug("Retrieving all users");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("All users:");
            int count = 0; // будет считать количество пользователей
            while (rs.next()) {
                System.out.printf("ID: %d, Username: %s, Email: %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"));
                count++;
            }
            logger.info("Retrieved {} users", count);

        } catch (SQLException e) {
            logger.error("Error getting all users: {}", e.getMessage());
            System.err.println("Error getting users: " + e.getMessage());
        }
    }

    // UPDATE  обновление email пользователя
    public static boolean updateUserEmail(int userId, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";
        logger.info("Updating email for user ID {} to: {}", userId, newEmail);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newEmail);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Successfully updated email for user ID: {}", userId);
            } else {
                logger.warn("No user found to update with ID: {}", userId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error updating user ID {}: {}", userId, e.getMessage());
            return false;
        }
    }

    // DELETE  удаление пользователя
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        logger.warn("Deleting user with ID: {}", userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.warn("Successfully deleted user ID: {}", userId);
            } else {
                logger.warn("No user found to delete with ID: {}", userId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error deleting user ID {}: {}", userId, e.getMessage());
            return false;
        }
    }
}