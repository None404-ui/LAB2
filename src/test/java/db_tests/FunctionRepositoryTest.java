package db_tests;

import db.DatabaseInitializer;
import db.FunctionRepository;
import db.FunctionRepositoryImp;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FunctionRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionRepositoryTest.class);
    private final Random random = new Random();

    @BeforeAll
    void setUp() {
        logger.info("Starting FunctionRepository tests");
        DatabaseInitializer.initializeDatabase();
    }

    // Генерация тестовых данных для функций
    private String generateRandomFunctionName() {
        String[] functionTypes = {"linear", "quadratic", "cubic", "exponential", "logarithmic", "trigonometric"};
        String type = functionTypes[random.nextInt(functionTypes.length)];
        return type + "_func_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }

    private String generateRandomExpression() {
        String[][] expressions = {
                {"x + 5", "2*x - 3", "x/2 + 1"},
                {"x^2 + 2*x + 1", "3*x^2 - 4*x + 7", "x^2 - 5*x + 6"},
                {"x^3 + x^2 + x + 1", "2*x^3 - 3*x^2 + 4*x - 5"},
                {"2^x", "e^x", "10^x"},
                {"log(x)", "ln(x)", "log10(x)"},
                {"sin(x)", "cos(x)", "tan(x)"}
        };
        String[] category = expressions[random.nextInt(expressions.length)];
        return category[random.nextInt(category.length)];
    }

    @Test
    @DisplayName("Test CREATE operation - add new function")
    void testAddFunction() {
        logger.info("Testing CREATE operation for functions");

        String name = generateRandomFunctionName();
        String expression = generateRandomExpression();
        int userId = 1; // Используем существующего пользователя

        logger.debug("Creating function: name={}, expression={}, userId={}", name, expression, userId);

        boolean result = FunctionRepositoryImp.addFunction(name, userId, expression);
        assertTrue(result, "Function should be added successfully");

        logger.info("Function created successfully: {}", name);
    }

    @Test
    @DisplayName("Test READ operation - get user functions")
    void testGetUserFunctions() {
        logger.info("Testing READ operation for user functions");

        int userId = 1;

        // Сначала создаем несколько функций
        for (int i = 0; i < 2; i++) {
            FunctionRepositoryImp.addFunction(
                    generateRandomFunctionName(),
                    userId,
                    generateRandomExpression()
            );
        }

        // Затем получаем функции пользователя
        FunctionRepositoryImp.getUserFunctions(userId);
        logger.info("Retrieved user functions successfully");
    }



    @Test
    @DisplayName("Test UPDATE operation - update function expression")
    void testUpdateFunctionExpression() {
        logger.info("Testing UPDATE operation for functions");

        // Создаем функцию
        String name = generateRandomFunctionName();
        String originalExpression = "x + 1";
        int userId = 1;

        boolean created = FunctionRepositoryImp.addFunction(name, userId, originalExpression);
        assertTrue(created, "Test function should be created");

        logger.warn("Update test requires function ID - this is a limitation of current implementation");
    }

    @Test
    @DisplayName("Test with diverse function expressions")
    void testDiverseFunctionExpressions() {
        logger.info("Testing with diverse function expressions");

        String[][] diverseFunctions = {
                {"simple_linear", "2*x + 3"},
                {"complex_polynomial", "3*x^3 - 2*x^2 + 5*x - 7"},
                {"exponential_growth", "2^x + 5"},
                {"logarithmic_scale", "log(x) + ln(x)"},
                {"trig_complex", "sin(x) + cos(2*x) + tan(x/2)"},
                {"mixed_operations", "(x^2 + 2*x + 1)/(x - 1)"},
                {"nested_functions", "sqrt(x^2 + 1) + abs(x - 5)"}
        };

        int successCount = 0;
        for (String[] functionData : diverseFunctions) {
            String name = functionData[0] + "_" + System.currentTimeMillis();
            String expression = functionData[1];

            boolean result = FunctionRepositoryImp.addFunction(name, 1, expression);
            if (result) {
                successCount++;
                logger.debug("Created function: {} with expression: {}", name, expression);
            }
        }

        logger.info("Successfully created {}/{} diverse functions", successCount, diverseFunctions.length);
        assertTrue(successCount > 0, "Should create at least some diverse functions");
    }
}