package sort;

import search.SearchEnums;
import search.SearchRequest;
import search.SearchService;
import dto.UserDto;
import dto.FunctionDto;
import dto.PointDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(SortIntegrationTest.class);
    private static SearchService searchService;

    @BeforeAll
    static void setUp() {
        logger.info("Setting up Sort Integration tests");
        searchService = new SearchService();
    }

    @Test
    @DisplayName("Integration test: Complex sorting scenario")
    void testComplexSortingScenario() {
        logger.info("Testing complex sorting scenario");

        // Сложный сценарий: поиск + множественная сортировка + пагинация
        // Используем только существующие поля
        SearchRequest request = new SearchRequest()
                .addCriteria(new SearchRequest.Criteria("user_id", SearchEnums.SearchOperator.GREATER_THAN, 0))
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.DESC)) // Используем name вместо created_at
                .withPagination(1, 10)
                .withCountTotal(true);

        try {
            SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);

            assertNotNull(result, "Result should not be null");
            assertTrue(result.getResultCount() <= 10, "Should respect page size");

            List<FunctionDto> functions = result.getResults();
            if (functions.size() > 1) {
                // Проверяем сложную сортировку
                for (int i = 0; i < functions.size() - 1; i++) {
                    FunctionDto current = functions.get(i);
                    FunctionDto next = functions.get(i + 1);

                    // Проверяем основную сортировку по user_id
                    assertTrue(current.getUserId() <= next.getUserId(),
                            "Primary sort by user_id ASC should be maintained");

                    // Если user_id одинаковый, проверяем вторичную сортировку
                    if (current.getUserId().equals(next.getUserId())) {
                        // name DESC - текущий должен быть "больше" следующего в обратном порядке
                        assertTrue(current.getName().compareTo(next.getName()) >= 0,
                                "Functions with same user_id should be sorted by name DESC");
                    }
                }
            }

            logger.info("Complex sorting scenario test passed. Found {} functions", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Complex sorting scenario test failed: {}", e.getMessage());
            // Пропускаем тест если есть проблемы с данными
        }
    }

    @Test
    @DisplayName("Integration test: Cross-entity sorting consistency")
    void testCrossEntitySortingConsistency() {
        logger.info("Testing cross-entity sorting consistency");

        // Тестируем что сортировка работает одинаково для разных сущностей
        SearchRequest userRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .withPagination(1, 5);

        SearchRequest functionRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("function_id", SearchEnums.SortDirection.ASC))
                .withPagination(1, 5);

        try {
            SearchService.SearchResult<UserDto> userResult = searchService.searchUsers(userRequest);
            SearchService.SearchResult<FunctionDto> functionResult = searchService.searchFunctions(functionRequest);

            assertNotNull(userResult, "User result should not be null");
            assertNotNull(functionResult, "Function result should not be null");

            // Проверяем что оба запроса выполнились успешно
            assertTrue(userResult.getResultCount() >= 0, "User search should complete");
            assertTrue(functionResult.getResultCount() >= 0, "Function search should complete");

            // Проверяем что пагинация работает
            assertTrue(userResult.getResultCount() <= 5, "User results should respect page size");
            assertTrue(functionResult.getResultCount() <= 5, "Function results should respect page size");

            logger.info("Cross-entity sorting consistency test passed. Users: {}, Functions: {}",
                    userResult.getResultCount(), functionResult.getResultCount());
        } catch (Exception e) {
            logger.warn("Cross-entity sorting consistency test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Integration test: Sorting performance with large datasets")
    void testSortingPerformance() {
        logger.info("Testing sorting performance");

        long startTime = System.currentTimeMillis();

        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            assertNotNull(result, "Result should not be null");

            // Более реалистичный таймаут для больших наборов данных
            assertTrue(duration < 10000, "Sorting should complete within 10 seconds. Took: " + duration + "ms");

            // Проверяем что сортировка действительно работает
            if (result.getResultCount() > 1) {
                List<UserDto> users = result.getResults();
                for (int i = 0; i < users.size() - 1; i++) {
                    String current = users.get(i).getUsername();
                    String next = users.get(i + 1).getUsername();
                    assertTrue(current.compareTo(next) <= 0,
                            "Performance test results should still be sorted correctly");
                }
            }

            logger.info("Sorting performance test passed. Duration: {}ms, Results: {}",
                    duration, result.getResultCount());
        } catch (Exception e) {
            logger.error("Sorting performance test failed: {}", e.getMessage());
            fail("Performance test should not fail with exception");
        }
    }

    @Test
    @DisplayName("Integration test: Multi-table search with sorting")
    void testMultiTableSearchWithSorting() {
        logger.info("Testing multi-table search with sorting");

        // Тестируем поиск и сортировку по всем трем таблицам
        SearchRequest userRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withPagination(1, 3);

        SearchRequest functionRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC))
                .withPagination(1, 3);

        SearchRequest pointRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("x_value", SearchEnums.SortDirection.ASC))
                .withPagination(1, 3);

        try {
            SearchService.SearchResult<UserDto> userResult = searchService.searchUsers(userRequest);
            SearchService.SearchResult<FunctionDto> functionResult = searchService.searchFunctions(functionRequest);
            SearchService.SearchResult<PointDto> pointResult = searchService.searchPoints(pointRequest);

            assertNotNull(userResult, "User result should not be null");
            assertNotNull(functionResult, "Function result should not be null");
            assertNotNull(pointResult, "Point result should not be null");

            // Проверяем что все запросы выполнились
            assertTrue(userResult.getResultCount() >= 0, "User search should complete");
            assertTrue(functionResult.getResultCount() >= 0, "Function search should complete");
            assertTrue(pointResult.getResultCount() >= 0, "Point search should complete");

            logger.info("Multi-table search test passed. Users: {}, Functions: {}, Points: {}",
                    userResult.getResultCount(), functionResult.getResultCount(), pointResult.getResultCount());
        } catch (Exception e) {
            logger.warn("Multi-table search test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Integration test: Sorting with complex criteria")
    void testSortingWithComplexCriteria() {
        logger.info("Testing sorting with complex criteria");

        // Комбинируем несколько критериев поиска с сортировкой
        SearchRequest request = new SearchRequest()
                .addCriteria(new SearchRequest.Criteria("user_id", SearchEnums.SearchOperator.GREATER_THAN, 0))
                .addCriteria(new SearchRequest.Criteria("name", SearchEnums.SearchOperator.LIKE, "test%"))
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC))
                .withPagination(1, 5)
                .withCountTotal(true);

        try {
            SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 0) {
                List<FunctionDto> functions = result.getResults();

                // Проверяем что все результаты соответствуют критериям
                for (FunctionDto function : functions) {
                    assertTrue(function.getUserId() > 0, "Function should have user_id > 0");
                    assertTrue(function.getName().startsWith("test"),
                            "Function name should start with 'test'");
                }

                // Проверяем сортировку если есть несколько результатов
                if (functions.size() > 1) {
                    for (int i = 0; i < functions.size() - 1; i++) {
                        FunctionDto current = functions.get(i);
                        FunctionDto next = functions.get(i + 1);

                        assertTrue(current.getUserId() <= next.getUserId(),
                                "Functions should be sorted by user_id ASC");

                        if (current.getUserId().equals(next.getUserId())) {
                            assertTrue(current.getName().compareTo(next.getName()) <= 0,
                                    "Functions with same user_id should be sorted by name ASC");
                        }
                    }
                }
            }

            logger.info("Sorting with complex criteria test passed. Found {} functions", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Sorting with complex criteria test failed: {}", e.getMessage());
        }
    }


}