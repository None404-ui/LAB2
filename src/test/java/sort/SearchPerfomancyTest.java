// test/SearchPerformanceTest.java
package sort;

import dto.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import search.SearchEnums;
import search.SearchRequest;
import search.SearchService;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchPerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(SearchPerformanceTest.class);
    private SearchService searchService;
    private List<PerformanceResult> performanceResults;

    static class PerformanceResult {
        String testName;
        String entityType;
        String sortType;
        int recordCount;
        long executionTimeMs;
        boolean withIndex;
        String notes;

        public PerformanceResult(String testName, String entityType, String sortType,
                                 int recordCount, long executionTimeMs, boolean withIndex, String notes) {
            this.testName = testName;
            this.entityType = entityType;
            this.sortType = sortType;
            this.recordCount = recordCount;
            this.executionTimeMs = executionTimeMs;
            this.withIndex = withIndex;
            this.notes = notes;
        }
    }

    @BeforeAll
    void setUp() {
        logger.info("Setting up search performance tests");
        searchService = new SearchService();
        performanceResults = new ArrayList<>();
    }

    @Test
    @DisplayName("Performance test: Single field sorting comparison")
    void testSingleFieldSortingPerformance() {
        logger.info("Testing single field sorting performance");

        // Тест 1: Сортировка по username ASC
        long startTime = System.nanoTime();
        SearchRequest request1 = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        SearchService.SearchResult<UserDto> result1 = searchService.searchUsers(request1);
        long endTime = System.nanoTime();
        long duration1 = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Single Field ASC", "users", "username ASC",
                result1.getResultCount(), duration1, true, "Basic single field sorting"
        ));

        // Тест 2: Сортировка по username DESC
        startTime = System.nanoTime();
        SearchRequest request2 = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.DESC))
                .withCountTotal(true);

        SearchService.SearchResult<UserDto> result2 = searchService.searchUsers(request2);
        endTime = System.nanoTime();
        long duration2 = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Single Field DESC", "users", "username DESC",
                result2.getResultCount(), duration2, true, "Single field descending"
        ));

        logger.info("Single field sorting performance: ASC={}ms, DESC={}ms", duration1, duration2);
    }

    @Test
    @DisplayName("Performance test: Multi-field sorting")
    void testMultiFieldSortingPerformance() {
        logger.info("Testing multi-field sorting performance");

        long startTime = System.nanoTime();
        SearchRequest request = new SearchRequest()
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(request);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Multi-Field", "functions", "user_id ASC, name ASC",
                result.getResultCount(), duration, true, "Two-field composite sorting"
        ));

        logger.info("Multi-field sorting performance: {}ms for {} functions", duration, result.getResultCount());
    }

    @Test
    @DisplayName("Performance test: Numeric vs String sorting")
    void testNumericVsStringSorting() {
        logger.info("Testing numeric vs string sorting performance");

        // Числовая сортировка
        long startTime = System.nanoTime();
        SearchRequest numericRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("x_value", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        SearchService.SearchResult<PointDto> numericResult = searchService.searchPoints(numericRequest);
        long endTime = System.nanoTime();
        long numericDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Numeric Sort", "points", "x_value ASC",
                numericResult.getResultCount(), numericDuration, true, "Numeric field sorting"
        ));

        // Строковая сортировка
        startTime = System.nanoTime();
        SearchRequest stringRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        SearchService.SearchResult<FunctionDto> stringResult = searchService.searchFunctions(stringRequest);
        endTime = System.nanoTime();
        long stringDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "String Sort", "functions", "name ASC",
                stringResult.getResultCount(), stringDuration, true, "String field sorting"
        ));

        logger.info("Numeric vs String sorting: Numeric={}ms, String={}ms", numericDuration, stringDuration);
    }

    @Test
    @DisplayName("Performance test: Sorting with vs without search criteria")
    void testSortingWithWithoutCriteria() {
        logger.info("Testing sorting performance with and without search criteria");

        // Сортировка без критериев поиска
        long startTime = System.nanoTime();
        SearchRequest simpleRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC));

        SearchService.SearchResult<UserDto> simpleResult = searchService.searchUsers(simpleRequest);
        long endTime = System.nanoTime();
        long simpleDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Sort Only", "users", "username ASC",
                simpleResult.getResultCount(), simpleDuration, true, "Pure sorting without filters"
        ));

        // Сортировка с критериями поиска
        startTime = System.nanoTime();
        SearchRequest filteredRequest = new SearchRequest()
                .addCriteria(new SearchRequest.Criteria("user_id", SearchEnums.SearchOperator.GREATER_THAN, 0))
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC));

        SearchService.SearchResult<UserDto> filteredResult = searchService.searchUsers(filteredRequest);
        endTime = System.nanoTime();
        long filteredDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Sort + Filter", "users", "username ASC",
                filteredResult.getResultCount(), filteredDuration, true, "Sorting with WHERE clause"
        ));

        logger.info("Sorting with vs without criteria: Simple={}ms, Filtered={}ms",
                simpleDuration, filteredDuration);
    }

    @Test
    @DisplayName("Performance test: Pagination impact on sorting")
    void testPaginationImpact() {
        logger.info("Testing pagination impact on sorting performance");

        // Без пагинации
        long startTime = System.nanoTime();
        SearchRequest noPagination = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withCountTotal(true);

        SearchService.SearchResult<UserDto> noPagResult = searchService.searchUsers(noPagination);
        long endTime = System.nanoTime();
        long noPagDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "No Pagination", "users", "username ASC",
                noPagResult.getResultCount(), noPagDuration, true, "Full dataset sorting"
        ));

        // С пагинацией
        startTime = System.nanoTime();
        SearchRequest withPagination = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC))
                .withPagination(1, 10)
                .withCountTotal(true);

        SearchService.SearchResult<UserDto> withPagResult = searchService.searchUsers(withPagination);
        endTime = System.nanoTime();
        long withPagDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "With Pagination", "users", "username ASC",
                withPagResult.getResultCount(), withPagDuration, true, "Limited results with pagination"
        ));

        logger.info("Pagination impact: NoPag={}ms, WithPag={}ms", noPagDuration, withPagDuration);
    }

    @Test
    @DisplayName("Performance test: Cross-entity sorting comparison")
    void testCrossEntitySortingPerformance() {
        logger.info("Testing sorting performance across different entities");

        // Пользователи
        long startTime = System.nanoTime();
        SearchRequest userRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("username", SearchEnums.SortDirection.ASC));

        SearchService.SearchResult<UserDto> userResult = searchService.searchUsers(userRequest);
        long endTime = System.nanoTime();
        long userDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "User Sorting", "users", "username ASC",
                userResult.getResultCount(), userDuration, true, "User entity sorting"
        ));

        // Функции
        startTime = System.nanoTime();
        SearchRequest functionRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.ASC));

        SearchService.SearchResult<FunctionDto> functionResult = searchService.searchFunctions(functionRequest);
        endTime = System.nanoTime();
        long functionDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Function Sorting", "functions", "name ASC",
                functionResult.getResultCount(), functionDuration, true, "Function entity sorting"
        ));

        // Точки
        startTime = System.nanoTime();
        SearchRequest pointRequest = new SearchRequest()
                .addSort(new SearchRequest.Sort("x_value", SearchEnums.SortDirection.ASC));

        SearchService.SearchResult<PointDto> pointResult = searchService.searchPoints(pointRequest);
        endTime = System.nanoTime();
        long pointDuration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Point Sorting", "points", "x_value ASC",
                pointResult.getResultCount(), pointDuration, true, "Point entity sorting"
        ));

        logger.info("Cross-entity sorting: Users={}ms, Functions={}ms, Points={}ms",
                userDuration, functionDuration, pointDuration);
    }

    @Test
    @DisplayName("Performance test: Complex multi-criteria sorting")
    void testComplexMultiCriteriaSorting() {
        logger.info("Testing complex multi-criteria sorting performance");

        long startTime = System.nanoTime();
        SearchRequest complexRequest = new SearchRequest()
                .addCriteria(new SearchRequest.Criteria("user_id", SearchEnums.SearchOperator.GREATER_THAN, 0))
                .addCriteria(new SearchRequest.Criteria("name", SearchEnums.SearchOperator.LIKE, "test%"))
                .addSort(new SearchRequest.Sort("user_id", SearchEnums.SortDirection.ASC))
                .addSort(new SearchRequest.Sort("name", SearchEnums.SortDirection.DESC))
                .withPagination(1, 10)
                .withCountTotal(true);

        SearchService.SearchResult<FunctionDto> result = searchService.searchFunctions(complexRequest);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        performanceResults.add(new PerformanceResult(
                "Complex Multi-Criteria", "functions", "user_id ASC, name DESC",
                result.getResultCount(), duration, true, "Multiple filters + multi-sort + pagination"
        ));

        logger.info("Complex multi-criteria sorting: {}ms for {} functions", duration, result.getResultCount());
    }

    @AfterAll
    void tearDown() {
        logger.info("Performance tests completed");
        printPerformanceResults();
        exportResultsToTable();
    }

    private void printPerformanceResults() {
        logger.info("=== PERFORMANCE RESULTS SUMMARY ===");
        logger.info("| {:<25} | {:<12} | {:<20} | {:>8} | {:>8} | {:<10} |",
                "Test Name", "Entity", "Sort Type", "Records", "Time(ms)", "Notes");
        logger.info("| {:-<25} | {:-<12} | {:-<20} | {:-<8} | {:-<8} | {:-<10} |",
                "", "", "", "", "", "");

        for (PerformanceResult result : performanceResults) {
            logger.info("| {:<25} | {:<12} | {:<20} | {:>8} | {:>8} | {:<10} |",
                    result.testName, result.entityType, result.sortType,
                    result.recordCount, result.executionTimeMs, result.notes);
        }
    }

    private void exportResultsToTable() {
        String fileName = "sorting_performance_results.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Заголовок CSV
            writer.println("Test Name,Entity Type,Sort Type,Record Count,Execution Time (ms),With Index,Notes");

            // Данные
            for (PerformanceResult result : performanceResults) {
                writer.printf("%s,%s,%s,%d,%d,%b,%s%n",
                        result.testName,
                        result.entityType,
                        result.sortType,
                        result.recordCount,
                        result.executionTimeMs,
                        result.withIndex,
                        result.notes);
            }

            logger.info("Performance results exported to: {}", fileName);

        } catch (Exception e) {
            logger.error("Error exporting performance results: {}", e.getMessage());
        }
    }
}