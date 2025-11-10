package search;

import dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transformer.DtoTransformer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public static class SearchResult<T> {
        private List<T> results;
        private int totalCount;
        private Integer page;
        private Integer pageSize;

        public SearchResult(List<T> results, int totalCount, Integer page, Integer pageSize) {
            this.results = results;
            this.totalCount = totalCount;
            this.page = page;
            this.pageSize = pageSize;
        }

        public List<T> getResults() { return results; }
        public int getTotalCount() { return totalCount; }
        public Integer getPage() { return page; }
        public Integer getPageSize() { return pageSize; }
        public int getResultCount() { return results != null ? results.size() : 0; }
        public int getTotalPages() {
            return pageSize != null && pageSize > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;
        }
    }

    public SearchResult<UserDto> searchUsers(SearchRequest request) {
        logger.info("Searching users with request: {}", request);
        return searchEntities("users", "user_id", request, UserDto.class);
    }

    public SearchResult<FunctionDto> searchFunctions(SearchRequest request) {
        logger.info("Searching functions with request: {}", request);
        return searchEntities("functions", "function_id", request, FunctionDto.class);
    }

    public SearchResult<PointDto> searchPoints(SearchRequest request) {
        logger.info("Searching points with request: {}", request);
        return searchEntities("computed_points", "point_id", request, PointDto.class);
    }

    private <T> SearchResult<T> searchEntities(String tableName, String idField,
                                               SearchRequest request, Class<T> dtoClass) {
        List<T> results = new ArrayList<>();
        int totalCount = 0;

        try (Connection conn = getConnection()) {
            // Строим SQL с учетом реальной структуры таблиц
            String sql = buildSafeSearchSql(tableName, request, idField);
            logger.debug("Executing SQL: {}", sql);

            // Выполняем запрос
            try (PreparedStatement pstmt = buildSafePreparedStatement(conn, sql, request, tableName);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    T dto = transformResultSet(rs, dtoClass);
                    if (dto != null) {
                        results.add(dto);
                    }
                }

                if (request.isCountTotal()) {
                    totalCount = getSafeTotalCount(conn, tableName, request);
                }
            }

            logger.info("Found {} {} with total {}", results.size(), tableName, totalCount);
            return new SearchResult<>(results, totalCount, request.getPage(), request.getPageSize());

        } catch (SQLException e) {
            logger.error("Search failed for {}: {}", tableName, e.getMessage());
            throw new RuntimeException("Search failed", e);
        }
    }

    // Безопасное построение SQL с проверкой существующих полей
    private String buildSafeSearchSql(String tableName, SearchRequest request, String idField) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);

        // Безопасный WHERE clause - только с существующими полями
        List<SearchRequest.Criteria> validCriteria = getValidCriteria(request.getSearchCriteria(), tableName);
        if (!validCriteria.isEmpty()) {
            sql.append(" WHERE ");
            appendSafeSearchConditions(sql, validCriteria);
        }

        // Безопасный ORDER BY clause - только с существующими полями
        List<SearchRequest.Sort> validSorts = getValidSorts(request.getSortCriteria(), tableName);
        if (!validSorts.isEmpty()) {
            sql.append(" ORDER BY ");
            for (int i = 0; i < validSorts.size(); i++) {
                SearchRequest.Sort sort = validSorts.get(i);
                sql.append(sort.getField()).append(" ").append(sort.getDirection().getSqlDirection());
                if (i < validSorts.size() - 1) {
                    sql.append(", ");
                }
            }
        } else {
            sql.append(" ORDER BY ").append(idField).append(" ASC");
        }

        // Pagination
        if (request.hasPagination()) {
            int offset = (request.getPage() - 1) * request.getPageSize();
            sql.append(" LIMIT ").append(request.getPageSize()).append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }

    // Получение только валидных критериев для таблицы
    private List<SearchRequest.Criteria> getValidCriteria(List<SearchRequest.Criteria> criteria, String tableName) {
        List<SearchRequest.Criteria> validCriteria = new ArrayList<>();
        for (SearchRequest.Criteria criterion : criteria) {
            if (criterion.isValid() && isFieldExists(criterion.getField(), tableName)) {
                validCriteria.add(criterion);
            } else {
                logger.warn("Skipping invalid criterion for table {}: {}", tableName, criterion);
            }
        }
        return validCriteria;
    }

    // Получение только валидных сортировок для таблицы
    private List<SearchRequest.Sort> getValidSorts(List<SearchRequest.Sort> sorts, String tableName) {
        List<SearchRequest.Sort> validSorts = new ArrayList<>();
        for (SearchRequest.Sort sort : sorts) {
            if (sort.getField() != null && isFieldExists(sort.getField(), tableName)) {
                validSorts.add(sort);
            } else {
                logger.warn("Skipping invalid sort for table {}: {}", tableName, sort);
            }
        }
        return validSorts;
    }

    // Проверка существования поля в таблице
    private boolean isFieldExists(String fieldName, String tableName) {
        // Список полей для каждой таблицы
        switch (tableName) {
            case "users":
                return fieldName.equals("user_id") || fieldName.equals("username") ||
                        fieldName.equals("email") || fieldName.equals("password_hash") ||
                        fieldName.equals("created_at");
            case "functions":
                return fieldName.equals("function_id") || fieldName.equals("name") ||
                        fieldName.equals("user_id") || fieldName.equals("expression") ||
                        fieldName.equals("created_at");
            case "computed_points":
                return fieldName.equals("point_id") || fieldName.equals("function_id") ||
                        fieldName.equals("x_value") || fieldName.equals("y_value") ||
                        fieldName.equals("computed_at");
            default:
                return false;
        }
    }

    private void appendSafeSearchConditions(StringBuilder sql, List<SearchRequest.Criteria> criteria) {
        for (int i = 0; i < criteria.size(); i++) {
            SearchRequest.Criteria criterion = criteria.get(i);
            sql.append(criterion.getField())
                    .append(" ")
                    .append(criterion.getOperator().getSqlOperator());

            if (criterion.getOperator().requiresValue()) {
                sql.append(" ?");
            }

            if (i < criteria.size() - 1) {
                sql.append(" AND ");
            }
        }
    }

    // Безопасное создание PreparedStatement - ИСПРАВЛЕННАЯ ВЕРСИЯ
    private PreparedStatement buildSafePreparedStatement(Connection conn, String sql,
                                                         SearchRequest request, String tableName) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // Устанавливаем параметры только для валидных критериев
        List<SearchRequest.Criteria> validCriteria = getValidCriteria(request.getSearchCriteria(), tableName);
        int paramIndex = 1;

        for (SearchRequest.Criteria criterion : validCriteria) {
            if (criterion.getOperator().requiresValue() && criterion.getValue() != null) {
                Object value = criterion.getValue();

                // Обработка оператора LIKE
                if (criterion.getOperator() == SearchEnums.SearchOperator.LIKE) {
                    value = value + "%"; // Добавляем wildcard для LIKE
                }

                // Устанавливаем значение в зависимости от типа
                if (value instanceof Integer) {
                    pstmt.setInt(paramIndex, (Integer) value);
                } else if (value instanceof Double) {
                    pstmt.setDouble(paramIndex, (Double) value);
                } else if (value instanceof String) {
                    pstmt.setString(paramIndex, (String) value);
                } else {
                    pstmt.setObject(paramIndex, value);
                }

                logger.debug("Set parameter {}: {} = {}", paramIndex, criterion.getField(), value);
                paramIndex++;
            }
        }

        return pstmt;
    }

    // Безопасный подсчет общего количества - ИСПРАВЛЕННАЯ ВЕРСИЯ
    private int getSafeTotalCount(Connection conn, String tableName, SearchRequest request) throws SQLException {
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName);

        List<SearchRequest.Criteria> validCriteria = getValidCriteria(request.getSearchCriteria(), tableName);
        if (!validCriteria.isEmpty()) {
            countSql.append(" WHERE ");
            appendSafeSearchConditions(countSql, validCriteria);
        }

        try (PreparedStatement pstmt = buildSafePreparedStatement(conn, countSql.toString(), request, tableName);
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T transformResultSet(ResultSet rs, Class<T> dtoClass) throws SQLException {
        if (dtoClass == UserDto.class) return (T) DtoTransformer.toUserDto(rs);
        if (dtoClass == FunctionDto.class) return (T) DtoTransformer.toFunctionDto(rs);
        if (dtoClass == PointDto.class) return (T) DtoTransformer.toPointDto(rs);
        return null;
    }

    private Connection getConnection() throws SQLException {
        return db.DatabaseConnection.getConnection();
    }
}