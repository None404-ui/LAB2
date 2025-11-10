package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class FunctionRepositoryImp {
    private static final Logger logger = LoggerFactory.getLogger(FunctionRepositoryImp.class);

    // CREATE добавление новой функции
    public static boolean addFunction(String name, int userId, String expression) {
        String sql = "INSERT INTO functions (name, user_id, expression) VALUES (?, ?, ?)";
        logger.info("Creating function: name={}, userId={}, expression={}", name, userId, expression);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, userId);
            pstmt.setString(3, expression);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int functionId = generatedKeys.getInt(1);
                        logger.info("Successfully created function: id={}, name={}, userId={}",
                                functionId, name, userId);
                    }
                }
            } else {
                logger.warn("Failed to create function: name={}", name);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error creating function {}: {}", name, e.getMessage());
            return false;
        }
    }

    // READ получение функций пользователя
    public static void getUserFunctions(int userId) {
        String sql = "SELECT f.*, u.username FROM functions f " +
                "JOIN users u ON f.user_id = u.user_id " +
                "WHERE f.user_id = ?";
        logger.debug("Retrieving functions for user ID: {}", userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Functions for user ID " + userId + ":");
            int count = 0; //количество функций конкретного пользователя
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Expression: %s, User: %s%n",
                        rs.getInt("function_id"),
                        rs.getString("name"),
                        rs.getString("expression"),
                        rs.getString("username"));
                count++;
            }
            logger.info("Retrieved {} functions for user ID: {}", count, userId);

        } catch (SQLException e) {
            logger.error("Error getting functions for user ID {}: {}", userId, e.getMessage());
            System.err.println("Error getting functions: " + e.getMessage());
        }
    }

    // UPDATE обновление выражения функции
    public static boolean updateFunctionExpression(int functionId, String newExpression) {
        String sql = "UPDATE functions SET expression = ? WHERE function_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newExpression);
            pstmt.setInt(2, functionId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Successfully updated function ID: {}", functionId);
            } else {
                logger.warn("No function found to update with ID: {}", functionId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error updating function ID {}: {}", functionId, e.getMessage());
            return false;
        }
    }

    // DELETE  удаление функции
    public static boolean deleteFunction(int functionId) {
        String sql = "DELETE FROM functions WHERE function_id = ?";
        logger.warn("Deleting function ID: {}", functionId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.warn("Successfully deleted function ID: {}", functionId);
            } else {
                logger.warn("No function found to delete with ID: {}", functionId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error deleting function ID {}: {}", functionId, e.getMessage());
            return false;
        }
    }
}