package db_tests;


import db.DatabaseInitializer;
import db.FunctionRepositoryImp;
import db.PointRepositoryImp;
import db.PointRepository;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PointRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PointRepositoryTest.class);
    private final Random random = new Random();
    private int testFunctionId = 1;

    @BeforeEach
    void beforeEachTest() {
        logger.info("Starting new test case");
        // Очищаем точки перед каждым тестом
        PointRepositoryImp.deletePointsByFunction(testFunctionId);
    }

    @BeforeAll
    void setUp() {
        logger.info("Starting PointRepository tests");
        DatabaseInitializer.initializeDatabase();

        // Создаем тестовую функцию для точек
        FunctionRepositoryImp.addFunction("test_function_points", 1, "x^2");
    }

    // Генерация тестовых данных для точек
    private double generateRandomX() {
        return random.nextDouble() * 20 - 10; // от -10 до 10
    }

    private double generateRandomY() {
        return random.nextDouble() * 100 - 50; // от -50 до 50
    }

    private double calculateYForX(double x, String functionType) {
        switch (functionType) {
            case "linear": return 2 * x + 3;
            case "quadratic": return x * x;
            case "cubic": return x * x * x;
            case "sin": return Math.sin(x);
            default: return x;
        }
    }

    @Test
    @DisplayName("Test CREATE operation - add new point")
    void testAddPoint() {
        logger.info("Testing CREATE operation for points");

        double x = generateRandomX();
        double y = generateRandomY();

        logger.debug("Creating point: functionId={}, x={}, y={}", testFunctionId, x, y);

        boolean result = PointRepositoryImp.addPoint(testFunctionId, x, y);
        assertTrue(result, "Point should be added successfully");

        logger.info("Point created successfully: ({}, {})", x, y);
    }

    @Test
    @DisplayName("Test READ operation - get function points")
    void testGetFunctionPoints() {
        logger.info("Testing READ operation for function points");

        // Сначала создаем несколько точек
        for (int i = 0; i < 5; i++) {
            PointRepositoryImp.addPoint(testFunctionId, i, i * i);
        }

        // Затем получаем точки функции
        PointRepositoryImp.getFunctionPoints(testFunctionId);
        logger.info("Retrieved function points successfully");
    }

    @Test
    @DisplayName("Test READ operation - get points in range")
    void testGetPointsInRange() {
        logger.info("Testing READ operation - points in range");

        // Создаем точки в определенном диапазоне
        for (int i = 0; i < 10; i++) {
            PointRepositoryImp.addPoint(testFunctionId, i, Math.sin(i));
        }

        // Ищем точки в диапазоне
        double minX = 2.0;
        double maxX = 7.0;
        logger.debug("Searching points in range [{}, {}]", minX, maxX);

        PointRepositoryImp.getPointsInRange(testFunctionId, minX, maxX);
        logger.info("Points in range search completed");
    }

    @Test
    @DisplayName("Test with diverse point distributions")
    void testDiversePointDistributions() {
        logger.info("Testing with diverse point distributions");

        String[] functionTypes = {"linear", "quadratic", "cubic", "sin"};

        for (String functionType : functionTypes) {
            logger.debug("Creating points for {} function", functionType);

            // Создаем новую функцию для каждого типа
            String functionName = "test_" + functionType + "_" + System.currentTimeMillis();
            FunctionRepositoryImp.addFunction(functionName, 1, functionType + "(x)");

            // Создаем точки для этой функции
            for (int i = 0; i < 5; i++) {
                double x = i * 0.5;
                double y = calculateYForX(x, functionType);

                PointRepositoryImp.addPoint(testFunctionId, x, y);
                logger.trace("Created point for {}: ({}, {})", functionType, x, y);
            }
        }

        logger.info("Created diverse point distributions for {} function types", functionTypes.length);
    }

    @Test
    @DisplayName("Test point operations with extreme values")
    void testExtremeValuePoints() {
        logger.info("Testing point operations with extreme values");

        double[][] extremePoints = {
                {0.0, 0.0},
                {Double.MIN_VALUE, Double.MIN_VALUE},
                {Double.MAX_VALUE / 1000, Double.MAX_VALUE / 1000}, // избегаем переполнения
                {-1000.0, 5000.0},
                {1.23456789, 9.87654321},
                {Math.PI, Math.E}
        };

        int successCount = 0;
        for (double[] point : extremePoints) {
            double x = point[0];
            double y = point[1];

            boolean result = PointRepositoryImp.addPoint(testFunctionId, x, y);
            if (result) {
                successCount++;
                logger.debug("Created extreme point: ({}, {})", x, y);
            }
        }

        logger.info("Successfully created {}/{} extreme value points", successCount, extremePoints.length);
        assertTrue(successCount > 0, "Should create at least some extreme value points");
    }

    @Test
    @DisplayName("Test bulk point operations")
    void testBulkPointOperations() {
        logger.info("Testing bulk point operations");

        int pointCount = 10;
        logger.debug("Creating {} points in bulk", pointCount);

        int successCount = 0;
        for (int i = 0; i < pointCount; i++) {
            double x = i * 0.1;
            double y = Math.sin(x) * Math.cos(x);

            boolean result = PointRepositoryImp.addPoint(testFunctionId, x, y);
            if (result) {
                successCount++;
            }
        }

        logger.info("Successfully created {}/{} points in bulk operation", successCount, pointCount);
        assertTrue(successCount >= pointCount * 0.8, "Should create most points in bulk operation");
    }
}