package db_tests;


import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import db.UserRepositoryImp;
import db.FunctionRepositoryImp;
import db.PointRepositoryImp;
import db.DatabaseInitializer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

//Интеграционный тест проверяет, как все компоненты работают вместе

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private final Random random = new Random();

    @BeforeAll
    void setUp() {
        logger.info("Starting integration tests");
        DatabaseInitializer.initializeDatabase();
    }

    @Test
    @DisplayName("Complete CRUD workflow test")
    void testCompleteCrudWorkflow() {
        logger.info("Testing complete CRUD workflow");

        // CREATE - создаем пользователя
        String username = "integration_test_user_" + System.currentTimeMillis();
        String email = "integration_test_" + System.currentTimeMillis() + "@example.com";
        String passwordHash = "integration_hash_" + System.currentTimeMillis();

        boolean userCreated = UserRepositoryImp.addUser(username, email, passwordHash);
        assertTrue(userCreated, "User should be created");
        logger.info("Step 1/5: User created - {}", username);

        // CREATE - создаем функцию для пользователя
        String functionName = "integration_test_function_" + System.currentTimeMillis();
        String expression = "x^2 + 2*x + 1";

        // В реальном приложении мы бы использовали ID созданного пользователя
        boolean functionCreated = FunctionRepositoryImp.addFunction(functionName, 1, expression);
        assertTrue(functionCreated, "Function should be created");
        logger.info("Step 2/5: Function created - {}", functionName);

        // CREATE - создаем точки для функции
        int pointsCreated = 0;
        for (int i = 0; i < 5; i++) {
            double x = i - 2; // от -2 до 2
            double y = x * x + 2 * x + 1;

            boolean pointCreated = PointRepositoryImp.addPoint(1, x, y);
            if (pointCreated) {
                pointsCreated++;
            }
        }

        assertTrue(pointsCreated > 0, "Should create at least some points");
        logger.info("Step 3/5: Created {} points", pointsCreated);

        // READ - читаем созданные данные
        logger.info("Step 4/5: Reading created data");
        UserRepositoryImp.getUserByUsername(username);
        FunctionRepositoryImp.getUserFunctions(1);
        PointRepositoryImp.getFunctionPoints(1);

        // READ - поиск в диапазоне
        PointRepositoryImp.getPointsInRange(1, -1.0, 1.0);

        logger.info("Step 5/5: CRUD workflow completed successfully");
    }


}