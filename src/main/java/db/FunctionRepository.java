package db;

import java.sql.*;

public class FunctionRepository {

    // CREATE добавление новой функции
    public static boolean addFunction(String name, int userId, String expression) {
        String sql = "INSERT INTO functions (name, user_id, expression) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, userId);
            pstmt.setString(3, expression);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error adding function: " + e.getMessage());
            return false;
        }
    }

    // READ получение функций пользователя
    public static void getUserFunctions(int userId) {
        String sql = "SELECT f.*, u.username FROM functions f " +
                "JOIN users u ON f.user_id = u.user_id " +
                "WHERE f.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Functions for user ID " + userId + ":");
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Expression: %s, User: %s%n",
                        rs.getInt("function_id"),
                        rs.getString("name"),
                        rs.getString("expression"),
                        rs.getString("username"));
            }

        } catch (SQLException e) {
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
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating function: " + e.getMessage());
            return false;
        }
    }

    // DELETE  удаление функции
    public static boolean deleteFunction(int functionId) {
        String sql = "DELETE FROM functions WHERE function_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting function: " + e.getMessage());
            return false;
        }
    }
}