// transformer/DtoTransformer.java
package transformer;

import dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DtoTransformer {
    private static final Logger logger = LoggerFactory.getLogger(DtoTransformer.class);

    // Трансформация ResultSet в UserDto
    public static UserDto toUserDto(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to UserDto");

        try {
            UserDto user = new UserDto();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt.toLocalDateTime());
            }

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                user.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            logger.debug("Successfully transformed UserDto: {}", user.getUsername());
            return user;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to UserDto: {}", e.getMessage());
            throw e;
        }
    }

    // Трансформация Map в UserDto (для веб-интерфейса)
    public static UserDto toUserDto(Map<String, String> params) {
        logger.debug("Transforming Map to UserDto with params: {}", params.keySet());

        try {
            UserDto user = new UserDto();

            if (params.containsKey("user_id")) {
                user.setUserId(Integer.parseInt(params.get("user_id")));
            }
            if (params.containsKey("username")) {
                user.setUsername(params.get("username"));
            }
            if (params.containsKey("email")) {
                user.setEmail(params.get("email"));
            }
            if (params.containsKey("password_hash")) {
                user.setPasswordHash(params.get("password_hash"));
            }

            logger.debug("Successfully transformed Map to UserDto: {}", user.getUsername());
            return user;

        } catch (Exception e) {
            logger.error("Error transforming Map to UserDto: {}", e.getMessage());
            throw new RuntimeException("Failed to transform Map to UserDto", e);
        }
    }

    // Трансформация ResultSet в FunctionDto
    public static FunctionDto toFunctionDto(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to FunctionDto");

        try {
            FunctionDto function = new FunctionDto();
            function.setFunctionId(rs.getInt("function_id"));
            function.setName(rs.getString("name"));
            function.setExpression(rs.getString("expression"));
            function.setUserId(rs.getInt("user_id"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                function.setCreatedAt(createdAt.toLocalDateTime());
            }

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                function.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            logger.debug("Successfully transformed FunctionDto: {}", function.getName());
            return function;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to FunctionDto: {}", e.getMessage());
            throw e;
        }
    }

    // Трансформация Map в FunctionDto
    public static FunctionDto toFunctionDto(Map<String, String> params) {
        logger.debug("Transforming Map to FunctionDto with params: {}", params.keySet());

        try {
            FunctionDto function = new FunctionDto();

            if (params.containsKey("function_id")) {
                function.setFunctionId(Integer.parseInt(params.get("function_id")));
            }
            if (params.containsKey("name")) {
                function.setName(params.get("name"));
            }
            if (params.containsKey("expression")) {
                function.setExpression(params.get("expression"));
            }
            if (params.containsKey("user_id")) {
                function.setUserId(Integer.parseInt(params.get("user_id")));
            }

            logger.debug("Successfully transformed Map to FunctionDto: {}", function.getName());
            return function;

        } catch (Exception e) {
            logger.error("Error transforming Map to FunctionDto: {}", e.getMessage());
            throw new RuntimeException("Failed to transform Map to FunctionDto", e);
        }
    }

    // Трансформация ResultSet в PointDto
    public static PointDto toPointDto(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to PointDto");

        try {
            PointDto point = new PointDto();
            point.setPointId(rs.getInt("point_id"));
            point.setFunctionId(rs.getInt("function_id"));
            point.setXValue(rs.getDouble("x_value"));
            point.setYValue(rs.getDouble("y_value"));

            Timestamp computedAt = rs.getTimestamp("computed_at");
            if (computedAt != null) {
                point.setComputedAt(computedAt.toLocalDateTime());
            }

            logger.debug("Successfully transformed PointDto for function: {}", point.getFunctionId());
            return point;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to PointDto: {}", e.getMessage());
            throw e;
        }
    }

    // Трансформация Map в PointDto
    public static PointDto toPointDto(Map<String, String> params) {
        logger.debug("Transforming Map to PointDto with params: {}", params.keySet());

        try {
            PointDto point = new PointDto();

            if (params.containsKey("point_id")) {
                point.setPointId(Integer.parseInt(params.get("point_id")));
            }
            if (params.containsKey("function_id")) {
                point.setFunctionId(Integer.parseInt(params.get("function_id")));
            }
            if (params.containsKey("x_value")) {
                point.setXValue(Double.parseDouble(params.get("x_value")));
            }
            if (params.containsKey("y_value")) {
                point.setYValue(Double.parseDouble(params.get("y_value")));
            }

            logger.debug("Successfully transformed Map to PointDto");
            return point;

        } catch (Exception e) {
            logger.error("Error transforming Map to PointDto: {}", e.getMessage());
            throw new RuntimeException("Failed to transform Map to PointDto", e);
        }
    }

    // Трансформация списка ResultSet в список DTO
    public static List<UserDto> toUserDtoList(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to List<UserDto>");
        List<UserDto> users = new ArrayList<>();

        try {
            while (rs.next()) {
                users.add(toUserDto(rs));
            }

            logger.debug("Successfully transformed {} users", users.size());
            return users;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to UserDto list: {}", e.getMessage());
            throw e;
        }
    }

    public static List<FunctionDto> toFunctionDtoList(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to List<FunctionDto>");
        List<FunctionDto> functions = new ArrayList<>();

        try {
            while (rs.next()) {
                functions.add(toFunctionDto(rs));
            }

            logger.debug("Successfully transformed {} functions", functions.size());
            return functions;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to FunctionDto list: {}", e.getMessage());
            throw e;
        }
    }

    public static List<PointDto> toPointDtoList(ResultSet rs) throws SQLException {
        logger.debug("Transforming ResultSet to List<PointDto>");
        List<PointDto> points = new ArrayList<>();

        try {
            while (rs.next()) {
                points.add(toPointDto(rs));
            }

            logger.debug("Successfully transformed {} computed points", points.size());
            return points;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to PointDto list: {}", e.getMessage());
            throw e;
        }
    }

    // Валидация DTO объектов
    public static boolean validateUserDto(UserDto user) {
        logger.debug("Validating UserDto: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            logger.warn("UserDto validation failed: username is empty");
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            logger.warn("UserDto validation failed: email is empty");
            return false;
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            logger.warn("UserDto validation failed: passwordHash is empty");
            return false;
        }

        logger.debug("UserDto validation successful");
        return true;
    }

    public static boolean validateFunctionDto(FunctionDto function) {
        logger.debug("Validating FunctionDto: {}", function.getName());

        if (function.getName() == null || function.getName().trim().isEmpty()) {
            logger.warn("FunctionDto validation failed: name is empty");
            return false;
        }
        if (function.getUserId() == null) {
            logger.warn("FunctionDto validation failed: userId is null");
            return false;
        }

        logger.debug("FunctionDto validation successful");
        return true;
    }

    public static boolean validatePointDto(PointDto point) {
        logger.debug("Validating PointDto for function: {}", point.getFunctionId());

        if (point.getFunctionId() == null) {
            logger.warn("PointDto validation failed: functionId is null");
            return false;
        }
        if (point.getXValue() == null) {
            logger.warn("PointDto validation failed: xValue is null");
            return false;
        }
        if (point.getYValue() == null) {
            logger.warn("PointDto validation failed: yValue is null");
            return false;
        }

        logger.debug("PointDto validation successful");
        return true;
    }
}