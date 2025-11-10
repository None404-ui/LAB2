package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class PointRepositoryImp {
    private static final Logger logger = LoggerFactory.getLogger(PointRepository.class);

    // CREATE добавление новой точки
    public static boolean addPoint(int functionId, double x, double y) {
        String sql = "INSERT INTO computed_points (function_id, x_value, y_value) VALUES (?, ?, ?)";
        logger.info("Creating point: functionId={}, x={}, y={}", functionId, x, y);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            pstmt.setDouble(2, x);
            pstmt.setDouble(3, y);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int pointId = generatedKeys.getInt(1);
                        logger.info("Successfully created point: id={}, functionId={}", pointId, functionId);
                    }
                }
            } else {
                logger.warn("Failed to create point for functionId: {}", functionId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error creating point for functionId {}: {}", functionId, e.getMessage());
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
        logger.debug("Retrieving points for function ID: {}", functionId);


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Points for function ID " + functionId + ":");
            int count = 0;
            while (rs.next()) {
                System.out.printf("Point ID: %d, X: %.2f, Y: %.2f, Function: %s%n",
                        rs.getInt("point_id"),
                        rs.getDouble("x_value"),
                        rs.getDouble("y_value"),
                        rs.getString("function_name"));
                count++;
            }
            logger.info("Retrieved {} points for function ID: {}", count, functionId);

        } catch (SQLException e) {
            logger.error("Error getting points for function ID {}: {}", functionId, e.getMessage());
            System.err.println("Error getting points: " + e.getMessage());
        }
    }

    // READ поиск точек по диапазону X
    public static void getPointsInRange(int functionId, double minX, double maxX) {
        String sql = "SELECT * FROM computed_points " +
                "WHERE function_id = ? AND x_value BETWEEN ? AND ? " +
                "ORDER BY x_value";
        logger.debug("Searching points in range: functionId={}, minX={}, maxX={}", functionId, minX, maxX);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);
            pstmt.setDouble(2, minX);
            pstmt.setDouble(3, maxX);
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("Points for function %d in range [%.2f, %.2f]:%n", functionId, minX, maxX);
            int count = 0;
            while (rs.next()) {
                System.out.printf("X: %.2f, Y: %.2f%n",
                        rs.getDouble("x_value"),
                        rs.getDouble("y_value"));
                count++;
            }
            logger.info("Found {} points in range for function ID: {}", count, functionId);

        } catch (SQLException e) {
            logger.error("Error getting points in range for function ID {}: {}", functionId, e.getMessage());
            System.err.println("Error getting points in range: " + e.getMessage());
        }
    }

    // UPDATE обновление Y значения точки
    public static boolean updatePointY(int pointId, double newY) {
        String sql = "UPDATE computed_points SET y_value = ? WHERE point_id = ?";
        logger.info("Updating point ID {} with new Y value: {}", pointId, newY);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newY);
            pstmt.setInt(2, pointId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Successfully updated point ID: {}", pointId);
            } else {
                logger.warn("No point found to update with ID: {}", pointId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error updating point ID {}: {}", pointId, e.getMessage());
            return false;
        }
    }

    // DELETE удаление точки
    public static boolean deletePoint(int pointId) {
        String sql = "DELETE FROM computed_points WHERE point_id = ?";
        logger.warn("Deleting point ID: {}", pointId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pointId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.warn("Successfully deleted point ID: {}", pointId);
            } else {
                logger.warn("No point found to delete with ID: {}", pointId);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Error deleting point ID {}: {}", pointId, e.getMessage());
            return false;
        }
    }

    // DELETE - удаление всех точек функции
    public static boolean deletePointsByFunction(int functionId) {
        String sql = "DELETE FROM computed_points WHERE function_id = ?";
        logger.warn("Deleting all points for function ID: {}", functionId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionId);

            int affectedRows = pstmt.executeUpdate();
            logger.warn("Deleted {} points for function ID: {}", affectedRows, functionId);
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error deleting points for function ID {}: {}", functionId, e.getMessage());
            return false;
        }
    }
}