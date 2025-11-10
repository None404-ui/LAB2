package db;

import java.sql.*;

public class PointRepository {

    // CREATE добавление новой точки
    public static boolean addPoint(int functionId, double x, double y) {
        String sql = "INSERT INTO computed_points (function_id, x_value, y_value) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            pstmt.setDouble(2, x);
            pstmt.setDouble(3, y);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error adding point: " + e.getMessage());
            return false;
        }
    }

    // READ получение точек функции
    public static void getFunctionPoints(int functionId) {
        String sql = "SELECT cp.*, f.name as function_name " +
                "FROM computed_points cp " +
                "JOIN functions f ON cp.function_id = f.function_id " +
                "WHERE cp.function_id = ? " +
                "ORDER BY cp.x_value";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Points for function ID " + functionId + ":");
            while (rs.next()) {
                System.out.printf("Point ID: %d, X: %.2f, Y: %.2f, Function: %s%n",
                        rs.getInt("point_id"),
                        rs.getDouble("x_value"),
                        rs.getDouble("y_value"),
                        rs.getString("function_name"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting points: " + e.getMessage());
        }
    }

    // READ поиск точек по диапазону X
    public static void getPointsInRange(int functionId, double minX, double maxX) {
        String sql = "SELECT * FROM computed_points " +
                "WHERE function_id = ? AND x_value BETWEEN ? AND ? " +
                "ORDER BY x_value";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            pstmt.setDouble(2, minX);
            pstmt.setDouble(3, maxX);
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("Points for function %d in range [%.2f, %.2f]:%n", functionId, minX, maxX);
            while (rs.next()) {
                System.out.printf("X: %.2f, Y: %.2f%n",
                        rs.getDouble("x_value"),
                        rs.getDouble("y_value"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting points in range: " + e.getMessage());
        }
    }

    // UPDATE обновление Y значения точки
    public static boolean updatePointY(int pointId, double newY) {
        String sql = "UPDATE computed_points SET y_value = ? WHERE point_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newY);
            pstmt.setInt(2, pointId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating point: " + e.getMessage());
            return false;
        }
    }

    // DELETE удаление точки
    public static boolean deletePoint(int pointId) {
        String sql = "DELETE FROM computed_points WHERE point_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pointId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting point: " + e.getMessage());
            return false;
        }
    }
}