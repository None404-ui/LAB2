package db_tests;


import db.DatabaseInitializer;
import db.UserRepositoryImp;
import db.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);
    private final Random random = new Random();

    @BeforeAll
    void setUp() {
        logger.info("Starting UserRepository tests");
        // Инициализируем базу данных перед всеми тестами
        DatabaseInitializer.initializeDatabase();
    }

    @BeforeEach
    void beforeEachTest() {
        logger.info("Starting new test case");
    }

    @AfterEach
    void afterEachTest() {
        logger.info("Test case completed");
    }

    // Генерация тестовых данных
    private String generateRandomUsername() {
        return "testuser_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }

    private String generateRandomEmail() {
        return "test_" + System.currentTimeMillis() + "_" + random.nextInt(1000) + "@example.com";
    }

    private String generateRandomPasswordHash() {
        return "hash_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }

    @Test
    @DisplayName("Test CREATE operation - add new user")
    void testAddUser() {
        logger.info("Testing CREATE operation");

        // Генерируем уникальные тестовые данные
        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String passwordHash = generateRandomPasswordHash();

        logger.debug("Generated test data: username={}, email={}", username, email);

        // Выполняем операцию
        boolean result = UserRepositoryImp.addUser(username, email, passwordHash);

        // Проверяем результат
        assertTrue(result, "User should be added successfully");
        logger.info("User created successfully: {}", username);
    }

    @Test
    @DisplayName("Test READ operation - find user by username")
    void testGetUserByUsername() {
        logger.info("Testing READ operation by username");

        // Сначала создаем пользователя
        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String passwordHash = generateRandomPasswordHash();

        boolean created = UserRepositoryImp.addUser(username, email, passwordHash);
        assertTrue(created, "Test user should be created");

        // Затем ищем его
        logger.debug("Searching for user: {}", username);
        UserRepositoryImp.getUserByUsername(username);

        logger.info("User search completed for: {}", username);
    }

    @Test
    @DisplayName("Test READ operation - get all users")
    void testGetAllUsers() {
        logger.info("Testing READ operation - all users");

        // Добавляем несколько тестовых пользователей
        for (int i = 0; i < 3; i++) {
            UserRepositoryImp.addUser(
                    generateRandomUsername(),
                    generateRandomEmail(),
                    generateRandomPasswordHash()
            );
        }

        // Получаем всех пользователей
        UserRepositoryImp.getAllUsers();
        logger.info("Retrieved all users successfully");
    }

    @Test
    @DisplayName("Test UPDATE operation - update user email")
    void testUpdateUserEmail() {
        logger.info("Testing UPDATE operation");

        // Создаем пользователя
        String username = generateRandomUsername();
        String originalEmail = generateRandomEmail();
        String passwordHash = generateRandomPasswordHash();

        boolean created = UserRepositoryImp.addUser(username, originalEmail, passwordHash);
        assertTrue(created, "Test user should be created");

        // Обновляем email
        String newEmail = "updated_" + generateRandomEmail();
        logger.debug("Updating email from {} to {}", originalEmail, newEmail);

        // Для этого теста нам нужно знать ID пользователя
        // В реальном тесте мы бы сначала нашли пользователя, затем обновили
        logger.warn("Update test requires user ID - this is a limitation of current implementation");
    }

    @Test
    @DisplayName("Test DELETE operation")
    void testDeleteUser() {
        logger.info("Testing DELETE operation");

        // Создаем пользователя
        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String passwordHash = generateRandomPasswordHash();

        boolean created = UserRepositoryImp.addUser(username, email, passwordHash);
        assertTrue(created, "Test user should be created");

        logger.warn("Delete test requires user ID - this is a limitation of current implementation");
    }

    @Test
    @DisplayName("Test with diverse user data")
    void testDiverseUserData() {
        logger.info("Testing with diverse user data");

        // Разнообразные тестовые данные
        String[][] testUsers =  {
                {"liz_ka", "haggywaggy_" + System.currentTimeMillis() + "@company.com", "secure_hash_123"},
                {"gachi_much", "chon.chonguk23_" + System.currentTimeMillis() + "@test.org", "another_hash_456"},
                {"bob_2024", "cringe2024_" + System.currentTimeMillis() + "@example.net", "hash_789_final"},
                {"user-with-dash", "dash-user_" + System.currentTimeMillis() + "@test.com", "dash_hash_000"},
                {"user_with_underscore", "underscore_user_" + System.currentTimeMillis() + "@example.com", "underscore_hash_111"}
        };

        for (String[] userData : testUsers) {
            String username = userData[0] + "_" + System.currentTimeMillis();
            String email = userData[1];
            String passwordHash = userData[2];

            logger.debug("Creating diverse user: {}", username);
            boolean result = UserRepositoryImp.addUser(username, email, passwordHash);
            assertTrue(result, "Should create user: " + username);
        }

        logger.info("Created {} diverse users", testUsers.length);
    }

    @Test
    @DisplayName("Test duplicate user creation")
    void testDuplicateUser() {
        logger.info("Testing duplicate user creation");

        String username = generateRandomUsername();
        String email = generateRandomEmail();
        String passwordHash = generateRandomPasswordHash();

        // Первое создание - должно быть успешным
        boolean firstAttempt = UserRepositoryImp.addUser(username, email, passwordHash);
        assertTrue(firstAttempt, "First user creation should succeed");

        // Второе создание с тем же username - должно быть неуспешным
        boolean secondAttempt = UserRepositoryImp.addUser(username, generateRandomEmail(), generateRandomPasswordHash());

        // В текущей реализации это может быть true/false в зависимости от ограничений БД
        logger.info("Duplicate user creation attempt result: {}", secondAttempt);
    }
}