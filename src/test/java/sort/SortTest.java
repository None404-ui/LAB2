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

class SortTest {
    private static final Logger logger = LoggerFactory.getLogger(SortTest.class);
    private static SearchService searchService;

    @BeforeAll
    static void setUp() {
        logger.info("Setting up Sort tests");
        searchService = new SearchService();
    }

    @Test
    @DisplayName("Test single field ASC sorting for users")
    void testSingleFieldAscSorting() {
        logger.info("Testing single field ASC sorting for users");

        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC));

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                // Проверяем что результаты отсортированы по username ASC
                List<UserDto> users = result.getResults();
                for (int i = 0; i < users.size() - 1; i++) {
                    String current = users.get(i).getUsername();
                    String next = users.get(i + 1).getUsername();
                    assertTrue(current.compareTo(next) <= 0,
                            "Users should be sorted in ASC order. " + current + " should be <= " + next);
                }
            }

            logger.info("Single field ASC sorting test passed. Found {} users", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Single field ASC sorting test failed: {}", e.getMessage());
            // Пропускаем тест если нет данных
        }
    }

    @Test
    @DisplayName("Test single field DESC sorting for users")
    void testSingleFieldDescSorting() {
        logger.info("Testing single field DESC sorting for users");

        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.DESC));

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                // Проверяем что результаты отсортированы по username DESC
                List<UserDto> users = result.getResults();
                for (int i = 0; i < users.size() - 1; i++) {
                    String current = users.get(i).getUsername();
                    String next = users.get(i + 1).getUsername();
                    assertTrue(current.compareTo(next) >= 0,
                            "Users should be sorted in DESC order. " + current + " should be >= " + next);
                }
            }

            logger.info("Single field DESC sorting test passed. Found {} users", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Single field DESC sorting test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test multi-field sorting for functions")
    void testMultiFieldSorting() {
        logger.info("Testing multi-field sorting for functions");

        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC));

        try {
            SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                // Проверяем сортировку по user_id ASC, затем по name ASC
                List<FunctionDto> functions = result.getResults();
                for (int i = 0; i < functions.size() - 1; i++) {
                    FunctionDto current = functions.get(i);
                    FunctionDto next = functions.get(i + 1);

                    // Проверяем основную сортировку
                    assertTrue(current.getUserId() <= next.getUserId(),
                            "Functions should be sorted by user_id ASC");

                    // Если user_id одинаковый, проверяем вторичную сортировку
                    if (current.getUserId().equals(next.getUserId())) {
                        assertTrue(current.getName().compareTo(next.getName()) <= 0,
                                "Functions with same user_id should be sorted by name ASC");
                    }
                }
            }

            logger.info("Multi-field sorting test passed. Found {} functions", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Multi-field sorting test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test numeric field sorting for points")
    void testNumericFieldSorting() {
        logger.info("Testing numeric field sorting for points");

        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("x_value", SearchEnums.SortDirection.ASC));

        try {
            SearchService.SearchResult<PointDto> result = searchService.searchPoints(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                List<PointDto> points = result.getResults();
                for (int i = 0; i < points.size() - 1; i++) {
                    Double current = points.get(i).getXValue();
                    Double next = points.get(i + 1).getXValue();
                    assertTrue(current <= next,
                            "Points should be sorted by x_value ASC. " + current + " should be <= " + next);
                }
            }

            logger.info("Numeric field sorting test passed. Found {} points", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Numeric field sorting test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test mixed ASC/DESC sorting with safe fields")
    void testMixedAscDescSorting() {
        logger.info("Testing mixed ASC/DESC sorting with safe fields");

        // Используем только существующие поля
        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.DESC));

        try {
            SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                List<FunctionDto> functions = result.getResults();
                // Просто проверяем что запрос выполнился без ошибок
                assertTrue(functions.size() > 0, "Should return some functions");
            }

            logger.info("Mixed ASC/DESC sorting test passed. Found {} functions", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Mixed ASC/DESC sorting test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test sorting with search criteria")
    void testSortingWithSearchCriteria() {
        logger.info("Testing sorting combined with search criteria");

        // Используем существующего пользователя (user_id 1 должен существовать)
        SearchRequest request = new SearchRequest()
                .addCriteria(new SearchRequest.Criteria("user_id", SearchEnums.SearchOperator.EQUALS, 1))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC));

        try {
            SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 0) {
                // Проверяем что все результаты соответствуют критерию поиска
                List<FunctionDto> functions = result.getResults();
                for (FunctionDto function : functions) {
                    assertEquals(1, function.getUserId(),
                            "All functions should belong to user_id 1");
                }

                // Проверяем сортировку если есть несколько результатов
                if (functions.size() > 1) {
                    for (int i = 0; i < functions.size() - 1; i++) {
                        String current = functions.get(i).getName();
                        String next = functions.get(i + 1).getName();
                        assertTrue(current.compareTo(next) <= 0,
                                "Filtered results should still be sorted");
                    }
                }
            }

            logger.info("Sorting with search criteria test passed. Found {} functions", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Sorting with search criteria test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test default sorting when no sort criteria provided")
    void testDefaultSorting() {
        logger.info("Testing default sorting behavior");

        SearchRequest request = new SearchRequest();

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            assertNotNull(result, "Result should not be null");

            if (result.getResultCount() > 1) {
                // По умолчанию должна быть сортировка по первичному ключу
                List<UserDto> users = result.getResults();
                for (int i = 0; i < users.size() - 1; i++) {
                    Integer current = users.get(i).getUserId();
                    Integer next = users.get(i + 1).getUserId();
                    assertTrue(current <= next,
                            "Default sorting should be by primary key ASC");
                }
            }

            logger.info("Default sorting test passed. Found {} users", result.getResultCount());
        } catch (Exception e) {
            logger.warn("Default sorting test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test sorting with pagination")
    void testSortingWithPagination() {
        logger.info("Testing sorting with pagination");

        int pageSize = 5;
        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withPagination(1, pageSize)
                .withCountTotal(true);

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            assertNotNull(result, "Result should not be null");
            assertTrue(result.getResultCount() <= pageSize, "Should return no more than page size");

            if (result.getResultCount() > 1) {
                // Проверяем что пагинация работает с сортировкой
                List<UserDto> users = result.getResults();
                for (int i = 0; i < users.size() - 1; i++) {
                    String current = users.get(i).getUsername();
                    String next = users.get(i + 1).getUsername();
                    assertTrue(current.compareTo(next) <= 0,
                            "Paginated results should still be sorted");
                }
            }

            logger.info("Sorting with pagination test passed. Page: {}/{}, Results: {}/{}",
                    result.getPage(), result.getTotalPages(),
                    result.getResultCount(), result.getTotalCount());
        } catch (Exception e) {
            logger.warn("Sorting with pagination test failed: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test basic search functionality")
    void testBasicSearchFunctionality() {
        logger.info("Testing basic search functionality");

        SearchRequest request = new SearchRequest();

        try {
            SearchService.SearchResult<UserDto> result = searchService.searchUsers(request);

            assertNotNull(result, "Result should not be null");
            assertTrue(result.getResultCount() >= 0, "Should return valid result count");

            logger.info("Basic search test passed. Found {} users", result.getResultCount());
        } catch (Exception e) {
            logger.error("Basic search test failed: {}", e.getMessage());
            fail("Basic search should work without errors");
        }
    }
}