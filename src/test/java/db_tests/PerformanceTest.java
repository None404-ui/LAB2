package db_tests;

import db.DatabaseInitializer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
    private final Random random = new Random();
    private static final int TOTAL_ITERATIONS = 10000;
    private static final int BATCH_SIZE = 1000;

    @BeforeAll
    void setUp() {
        logger.info("Setting up performance tests");
        DatabaseInitializer.initializeDatabase();
        createBenchmarksTable();
        generateTestDataIfNeeded();
        clearOldBenchmarks();
    }

    private void createBenchmarksTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS benchmark_results (
                benchmark_id SERIAL PRIMARY KEY,
                test_name VARCHAR(100) NOT NULL,
                table_name VARCHAR(50) NOT NULL,
                record_count INTEGER NOT NULL,
                operation_type VARCHAR(20) NOT NULL,
                execution_time_ms BIGINT NOT NULL,
                iteration_number INTEGER NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Benchmark results table created");
        } catch (SQLException e) {
            logger.error("Error creating benchmarks table: {}", e.getMessage());
        }
    }

    private void clearOldBenchmarks() {
        String sql = "DELETE FROM benchmark_results";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Cleared old benchmark results");
        } catch (SQLException e) {
            logger.error("Error clearing old benchmarks: {}", e.getMessage());
        }
    }

    private void generateTestDataIfNeeded() {
        if (getRecordCount("users") < 10000) {
            logger.info("Generating test data for performance testing...");
            generateTestData();
        } else {
            logger.info("Using existing test data ({} users found)", getRecordCount("users"));
        }
    }

    private void generateTestData() {
        generateUsers(10000);
        generateFunctions(10000);
        generatePoints(10000);
    }

    private void generateUsers(int count) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        logger.info("Generating {} users", count);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < count; i++) {
                pstmt.setString(1, "perf_user_" + i);
                pstmt.setString(2, "user" + i + "@perf.com");
                pstmt.setString(3, "hash_" + i);
                pstmt.addBatch();

                if (i % 1000 == 0) {
                    pstmt.executeBatch();
                    logger.info("Generated {} users", i);
                }
            }
            pstmt.executeBatch();
            logger.info("Completed generating {} users", count);

        } catch (SQLException e) {
            logger.error("Error generating users: {}", e.getMessage());
        }
    }

    private void generateFunctions(int count) {
        String sql = "INSERT INTO functions (name, user_id, expression) VALUES (?, ?, ?)";
        logger.info("Generating {} functions", count);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] expressions = {"x^2", "2*x+1", "sin(x)", "x^3", "log(x)"};

            for (int i = 0; i < count; i++) {
                pstmt.setString(1, "func_" + i);
                pstmt.setInt(2, (i % 1000) + 1);
                pstmt.setString(3, expressions[i % expressions.length]);
                pstmt.addBatch();

                if (i % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }
            pstmt.executeBatch();
            logger.info("Completed generating {} functions", count);

        } catch (SQLException e) {
            logger.error("Error generating functions: {}", e.getMessage());
        }
    }

    private void generatePoints(int count) {
        String sql = "INSERT INTO computed_points (function_id, x_value, y_value) VALUES (?, ?, ?)";
        logger.info("Generating {} points", count);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < count; i++) {
                pstmt.setInt(1, (i % 500) + 1);
                pstmt.setDouble(2, random.nextDouble() * 100);
                pstmt.setDouble(3, random.nextDouble() * 1000);
                pstmt.addBatch();

                if (i % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }
            pstmt.executeBatch();
            logger.info("Completed generating {} points", count);

        } catch (SQLException e) {
            logger.error("Error generating points: {}", e.getMessage());
        }
    }

    private void saveBenchmarkResult(String testName, String tableName,
                                     int recordCount, String operationType,
                                     long executionTime, int iteration) {
        String sql = """
            INSERT INTO benchmark_results 
            (test_name, table_name, record_count, operation_type, execution_time_ms, iteration_number) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, testName);
            pstmt.setString(2, tableName);
            pstmt.setInt(3, recordCount);
            pstmt.setString(4, operationType);
            pstmt.setLong(5, executionTime);
            pstmt.setInt(6, iteration);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error saving benchmark result: {}", e.getMessage());
        }
    }

    private void saveBenchmarkBatch(String testName, String tableName,
                                    String operationType, long[] executionTimes) {
        String sql = """
            INSERT INTO benchmark_results 
            (test_name, table_name, record_count, operation_type, execution_time_ms, iteration_number) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < executionTimes.length; i++) {
                pstmt.setString(1, testName);
                pstmt.setString(2, tableName);
                pstmt.setInt(3, 1);
                pstmt.setString(4, operationType);
                pstmt.setLong(5, executionTimes[i]);
                pstmt.setInt(6, i + 1);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            logger.error("Error saving benchmark batch: {}", e.getMessage());
        }
    }

    private int getRecordCount(String tableName) {
        String sql = "SELECT COUNT(*) as count FROM " + tableName;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt("count") : 0;

        } catch (SQLException e) {
            logger.error("Error counting records in {}: {}", tableName, e.getMessage());
            return 0;
        }
    }

    // ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ

    @Test
    @DisplayName("Performance test: SELECT by primary key")
    void testSelectByIdPerformance() {
        String testName = "SELECT_BY_ID";
        logger.info("Starting {} with {} iterations", testName, TOTAL_ITERATIONS);

        String sql = "SELECT * FROM users WHERE user_id = ?";
        long[] executionTimes = new long[TOTAL_ITERATIONS];

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < TOTAL_ITERATIONS; i++) {
                int randomId = random.nextInt(10000) + 1;
                pstmt.setInt(1, randomId);

                long startTime = System.nanoTime();
                ResultSet rs = pstmt.executeQuery();
                long endTime = System.nanoTime();

                executionTimes[i] = (endTime - startTime) / 1_000_000; // в миллисекундах
                rs.close();

                if ((i + 1) % 1000 == 0) {
                    logger.info("{}: Completed {} iterations", testName, i + 1);
                }
            }

            // Сохраняем все измерения пакетно
            saveBenchmarkBatch(testName, "users", "SELECT", executionTimes);

            // Сохраняем статистику
            saveStatistics(testName, executionTimes);

        } catch (SQLException e) {
            logger.error("Error in SELECT performance test: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Performance test: SELECT with WHERE clause")
    void testSelectWithWherePerformance() {
        String testName = "SELECT_WITH_WHERE";
        logger.info("Starting {} with {} iterations", testName, TOTAL_ITERATIONS);

        String sql = "SELECT * FROM users WHERE username LIKE ?";
        long[] executionTimes = new long[TOTAL_ITERATIONS];

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < TOTAL_ITERATIONS; i++) {
                pstmt.setString(1, "perf_user_" + (random.nextInt(100) * 100) + "%");

                long startTime = System.nanoTime();
                ResultSet rs = pstmt.executeQuery();
                long endTime = System.nanoTime();

                executionTimes[i] = (endTime - startTime) / 1_000_000;
                rs.close();

                if ((i + 1) % 1000 == 0) {
                    logger.info("{}: Completed {} iterations", testName, i + 1);
                }
            }

            saveBenchmarkBatch(testName, "users", "SELECT", executionTimes);
            saveStatistics(testName, executionTimes);

        } catch (SQLException e) {
            logger.error("Error in SELECT WHERE performance test: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Performance test: BULK INSERT")
    void testBulkInsertPerformance() {
        String testName = "BULK_INSERT";
        int batchSize = 10000;

        logger.info("Starting {} with {} records", testName, batchSize);

        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        long[] executionTimes = new long[batchSize];

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            long batchStartTime = System.nanoTime();

            for (int i = 0; i < batchSize; i++) {
                long startTime = System.nanoTime();

                pstmt.setString(1, "bulk_user_" + System.currentTimeMillis() + "_" + i);
                pstmt.setString(2, "bulk" + i + "_" + System.currentTimeMillis() + "@test.com");
                pstmt.setString(3, "bulk_hash_" + i);
                pstmt.executeUpdate();

                long endTime = System.nanoTime();
                executionTimes[i] = (endTime - startTime) / 1_000_000;

                if ((i + 1) % 1000 == 0) {
                    logger.info("{}: Completed {} records", testName, i + 1);
                }
            }

            long totalBatchTime = (System.nanoTime() - batchStartTime) / 1_000_000;
            logger.info("{}: Total batch time {} ms", testName, totalBatchTime);

            saveBenchmarkBatch(testName, "users", "INSERT", executionTimes);
            saveStatistics(testName, executionTimes);

        } catch (SQLException e) {
            logger.error("Error in bulk insert performance test: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Performance test: Complex JOIN query")
    void testComplexJoinPerformance() {
        String testName = "COMPLEX_JOIN";
        logger.info("Starting {} with {} iterations", testName, TOTAL_ITERATIONS);

        String sql = """
            SELECT u.username, f.name, COUNT(cp.point_id) as point_count 
            FROM users u 
            JOIN functions f ON u.user_id = f.user_id 
            JOIN computed_points cp ON f.function_id = cp.function_id 
            GROUP BY u.user_id, u.username, f.function_id, f.name 
            HAVING COUNT(cp.point_id) > 2 
            LIMIT 20
            """;

        long[] executionTimes = new long[TOTAL_ITERATIONS];

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (int i = 0; i < TOTAL_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                ResultSet rs = stmt.executeQuery(sql);
                long endTime = System.nanoTime();

                executionTimes[i] = (endTime - startTime) / 1_000_000;
                rs.close();

                if ((i + 1) % 1000 == 0) {
                    logger.info("{}: Completed {} iterations", testName, i + 1);
                }
            }

            saveBenchmarkBatch(testName, "multiple", "SELECT", executionTimes);
            saveStatistics(testName, executionTimes);

        } catch (SQLException e) {
            logger.error("Error in complex join performance test: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Performance test: Aggregation query")
    void testAggregationPerformance() {
        String testName = "AGGREGATION";
        logger.info("Starting {} with {} iterations", testName, TOTAL_ITERATIONS);

        String sql = """
            SELECT function_id, COUNT(*) as point_count, 
                   AVG(x_value) as avg_x, AVG(y_value) as avg_y 
            FROM computed_points 
            GROUP BY function_id 
            HAVING COUNT(*) > 2
            """;

        long[] executionTimes = new long[TOTAL_ITERATIONS];

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (int i = 0; i < TOTAL_ITERATIONS; i++) {
                long startTime = System.nanoTime();
                ResultSet rs = stmt.executeQuery(sql);
                long endTime = System.nanoTime();

                executionTimes[i] = (endTime - startTime) / 1_000_000;
                rs.close();

                if ((i + 1) % 1000 == 0) {
                    logger.info("{}: Completed {} iterations", testName, i + 1);
                }
            }

            saveBenchmarkBatch(testName, "computed_points", "SELECT", executionTimes);
            saveStatistics(testName, executionTimes);

        } catch (SQLException e) {
            logger.error("Error in aggregation performance test: {}", e.getMessage());
        }
    }

    private void saveStatistics(String testName, long[] executionTimes) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;

        for (long time : executionTimes) {
            if (time < min) min = time;
            if (time > max) max = time;
            sum += time;
        }

        long avg = sum / executionTimes.length;

        logger.info("{} STATISTICS: Min={}ms, Max={}ms, Avg={}ms, Total={}ms",
                testName, min, max, avg, sum);

        // Сохраняем статистику в отдельную запись
        saveBenchmarkResult(testName + "_STATS", "statistics", executionTimes.length,
                "STATS", avg, 0);
    }

    @AfterAll
    void tearDown() {
        logger.info("Performance tests completed");
        verifyResults();
        exportBenchmarkResults();
        exportToCsv();
    }

    private void verifyResults() {
        String countSql = "SELECT COUNT(*) as total_count FROM benchmark_results";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next()) {
                int totalRecords = rs.getInt("total_count");
                logger.info("=== VERIFICATION ===");
                logger.info("TOTAL BENCHMARK RECORDS: {}", totalRecords);

                int expectedMinRecords = TOTAL_ITERATIONS * 4; // 4 теста по 10000 итераций
                if (totalRecords < expectedMinRecords) {
                    logger.warn("WARNING: Only {} records found. Expected at least {}",
                            totalRecords, expectedMinRecords);
                } else {
                    logger.info("SUCCESS: Collected {} benchmark records", totalRecords);
                }
            }

        } catch (SQLException e) {
            logger.error("Error counting benchmark results: {}", e.getMessage());
        }
    }

    private void exportBenchmarkResults() {
        logger.info("=== FINAL BENCHMARK RESULTS ===");

        String sql = """
            SELECT test_name, COUNT(*) as measurement_count, 
                   MIN(execution_time_ms) as min_time, 
                   MAX(execution_time_ms) as max_time,
                   AVG(execution_time_ms) as avg_time
            FROM benchmark_results 
            WHERE test_name NOT LIKE '%_STATS'
            GROUP BY test_name
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logger.info("{} | {} measurements | Min: {}ms | Max: {}ms | Avg: {}ms",
                        rs.getString("test_name"),
                        rs.getInt("measurement_count"),
                        rs.getLong("min_time"),
                        rs.getLong("max_time"),
                        rs.getLong("avg_time")
                );
            }

        } catch (SQLException e) {
            logger.error("Error exporting benchmark results: {}", e.getMessage());
        }
    }

    private void exportToCsv() {
        String fileName = "benchmark_results.csv";

        String sql = """
            SELECT test_name, table_name, record_count, operation_type, 
                   execution_time_ms, iteration_number, timestamp 
            FROM benchmark_results 
            ORDER BY test_name, iteration_number
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            // Заголовок CSV
            writer.println("test_name,table_name,record_count,operation_type,execution_time_ms,iteration_number,timestamp");

            int rowCount = 0;
            while (rs.next()) {
                writer.printf("%s,%s,%d,%s,%d,%d,%s%n",
                        rs.getString("test_name"),
                        rs.getString("table_name"),
                        rs.getInt("record_count"),
                        rs.getString("operation_type"),
                        rs.getLong("execution_time_ms"),
                        rs.getInt("iteration_number"),
                        rs.getTimestamp("timestamp")
                );
                rowCount++;
            }

            logger.info("Exported {} benchmark results to {}", rowCount, fileName);

            File file = new File(fileName);
            if (file.exists()) {
                logger.info("File saved to: {} ({} bytes)", file.getAbsolutePath(), file.length());
            }

        } catch (Exception e) {
            logger.error("Error exporting to CSV: {}", e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return db.DatabaseConnection.getConnection();
    }
}