// test/DtoTransformerTest.java
package dto_tests;

import dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transformer.DtoTransformer;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoTransformerTest {
    private static final Logger logger = LoggerFactory.getLogger(DtoTransformerTest.class);

    @Test
    @DisplayName("Transform Map to UserDto")
    void testToUserDtoFromMap() {
        logger.debug("Testing Map to UserDto transformation");

        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "1");
        params.put("username", "mapuser");
        params.put("email", "map@example.com");
        params.put("password_hash", "map_hash");

        // Act
        UserDto user = DtoTransformer.toUserDto(params);

        // Assert
        assertNotNull(user);
        assertEquals(1, user.getUserId());
        assertEquals("mapuser", user.getUsername());
        assertEquals("map@example.com", user.getEmail());
        assertEquals("map_hash", user.getPasswordHash());

        logger.info("Successfully transformed Map to UserDto: {}", user);
    }

    @Test
    @DisplayName("Transform Map to FunctionDto")
    void testToFunctionDtoFromMap() {
        logger.debug("Testing Map to FunctionDto transformation");

        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("function_id", "1");
        params.put("name", "map_function");
        params.put("expression", "2*x+1");
        params.put("user_id", "2");

        // Act
        FunctionDto function = DtoTransformer.toFunctionDto(params);

        // Assert
        assertNotNull(function);
        assertEquals(1, function.getFunctionId());
        assertEquals("map_function", function.getName());
        assertEquals("2*x+1", function.getExpression());
        assertEquals(2, function.getUserId());

        logger.info("Successfully transformed Map to FunctionDto: {}", function);
    }

    @Test
    @DisplayName("Transform Map to PointDto")
    void testToPointDtoFromMap() {
        logger.debug("Testing Map to PointDto transformation");

        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("point_id", "1");
        params.put("function_id", "1");
        params.put("x_value", "3.0");
        params.put("y_value", "9.0");

        // Act
        PointDto point = DtoTransformer.toPointDto(params);

        // Assert
        assertNotNull(point);
        assertEquals(1, point.getPointId());
        assertEquals(1, point.getFunctionId());
        assertEquals(3.0, point.getXValue());
        assertEquals(9.0, point.getYValue());

        logger.info("Successfully transformed Map to PointDto: {}", point);
    }

    @Test
    @DisplayName("DTO validation - valid objects")
    void testDtoValidationValid() {
        logger.debug("Testing DTO validation with valid objects");

        // Arrange
        UserDto validUser = new UserDto("validuser", "valid@example.com", "hash");
        FunctionDto validFunction = new FunctionDto("validfunc", "x+1", 1);
        PointDto validPoint = new PointDto(1, 2.0, 4.0);

        // Act & Assert
        assertTrue(DtoTransformer.validateUserDto(validUser));
        assertTrue(DtoTransformer.validateFunctionDto(validFunction));
        assertTrue(DtoTransformer.validatePointDto(validPoint));

        logger.info("DTO validation successful for valid objects");
    }

    @Test
    @DisplayName("DTO validation - invalid objects")
    void testDtoValidationInvalid() {
        logger.debug("Testing DTO validation with invalid objects");

        // Arrange
        UserDto invalidUser = new UserDto("", "invalid@example.com", "hash");
        FunctionDto invalidFunction = new FunctionDto("", "x+1", null);
        PointDto invalidPoint = new PointDto(null, null, 4.0);

        // Act & Assert
        assertFalse(DtoTransformer.validateUserDto(invalidUser));
        assertFalse(DtoTransformer.validateFunctionDto(invalidFunction));
        assertFalse(DtoTransformer.validatePointDto(invalidPoint));

        logger.info("DTO validation correctly identified invalid objects");
    }

    @Test
    @DisplayName("DTO equals and hashCode methods")
    void testDtoEqualsAndHashCode() {
        logger.debug("Testing DTO equals and hashCode methods");

        // Arrange
        UserDto user1 = new UserDto("user1", "user1@test.com", "hash1");
        user1.setUserId(1);

        UserDto user2 = new UserDto("user1", "user1@test.com", "hash1");
        user2.setUserId(1);

        UserDto user3 = new UserDto("user2", "user2@test.com", "hash2");
        user3.setUserId(2);

        // Assert
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());

        logger.info("Successfully tested equals and hashCode for UserDto");
    }

    @Test
    @DisplayName("DTO toString methods")
    void testDtoToString() {
        logger.debug("Testing DTO toString methods");

        // Arrange
        UserDto user = new UserDto("testuser", "test@example.com", "hash");
        user.setUserId(1);

        // Act
        String toString = user.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));

        logger.info("UserDto toString: {}", toString);
    }

    @Test
    @DisplayName("Transform with missing fields in Map")
    void testTransformWithMissingFields() {
        logger.debug("Testing transformation with missing fields in Map");

        // Arrange
        Map<String, String> minimalParams = new HashMap<>();
        minimalParams.put("username", "minimaluser");

        // Act
        UserDto user = DtoTransformer.toUserDto(minimalParams);

        // Assert
        assertNotNull(user);
        assertEquals("minimaluser", user.getUsername());
        assertNull(user.getUserId());
        assertNull(user.getEmail());

        logger.info("Successfully transformed Map with missing fields: {}", user);
    }

    @Test
    @DisplayName("Test DTO constructors")
    void testDtoConstructors() {
        logger.debug("Testing DTO constructors");

        // Test constructor with parameters
        UserDto user = new UserDto("constructor_user", "constructor@test.com", "pass_hash");
        assertEquals("constructor_user", user.getUsername());
        assertEquals("constructor@test.com", user.getEmail());
        assertEquals("pass_hash", user.getPasswordHash());

        FunctionDto function = new FunctionDto("constructor_func", "x^2", 1);
        assertEquals("constructor_func", function.getName());
        assertEquals("x^2", function.getExpression());
        assertEquals(1, function.getUserId());

        PointDto point = new PointDto(1, 2.0, 4.0);
        assertEquals(1, point.getFunctionId());
        assertEquals(2.0, point.getXValue());
        assertEquals(4.0, point.getYValue());

        logger.info("DTO constructors work correctly");
    }

    @Test
    @DisplayName("Test timestamp field handling")
    void testTimestampHandling() {
        logger.debug("Testing timestamp field handling");

        // Arrange
        UserDto user = new UserDto();
        LocalDateTime now = LocalDateTime.now();

        // Act
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Assert
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());

        logger.info("Timestamp field handling works correctly");
    }
}