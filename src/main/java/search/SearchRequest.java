package search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchRequest {
    private static final Logger logger = LoggerFactory.getLogger(SearchRequest.class);

    // Вложенный класс для критериев поиска
    public static class Criteria {
        private String field;
        private Object value;
        private SearchEnums.SearchOperator operator;
        private List<Criteria> nestedCriteria;
        private SearchEnums.LogicalOperator logicalOperator;

        public Criteria() {
            this.nestedCriteria = new ArrayList<>();
        }

        public Criteria(String field, SearchEnums.SearchOperator operator, Object value) {
            this();
            this.field = field;
            this.operator = operator;
            this.value = value;
            logger.debug("Created Criteria: {} {} {}", field, operator, value);
        }

        // Геттеры
        public String getField() { return field; }
        public Object getValue() { return value; }
        public SearchEnums.SearchOperator getOperator() { return operator; }
        public List<Criteria> getNestedCriteria() { return nestedCriteria; }

        // Builder методы
        public Criteria and(Criteria criteria) {
            criteria.logicalOperator = SearchEnums.LogicalOperator.AND;
            this.nestedCriteria.add(criteria);
            return this;
        }

        public Criteria or(Criteria criteria) {
            criteria.logicalOperator = SearchEnums.LogicalOperator.OR;
            this.nestedCriteria.add(criteria);
            return this;
        }

        public boolean hasNestedCriteria() {
            return !nestedCriteria.isEmpty();
        }

        public boolean isValid() {
            return field != null && !field.trim().isEmpty() && operator != null;
        }
    }

    // Вложенный класс для сортировки
    public static class Sort {
        private String field;
        private SearchEnums.SortDirection direction;

        public Sort() {}

        public Sort(String field, SearchEnums.SortDirection direction) {
            this.field = field;
            this.direction = direction;
            logger.debug("Created Sort: {} {}", field, direction);
        }

        public String getField() { return field; }
        public SearchEnums.SortDirection getDirection() { return direction; }
    }

    // Основные поля SearchRequest
    private List<Criteria> searchCriteria;
    private List<Sort> sortCriteria;
    private Integer page;
    private Integer pageSize;
    private boolean countTotal;

    public SearchRequest() {
        this.searchCriteria = new ArrayList<>();
        this.sortCriteria = new ArrayList<>();
        logger.debug("Created SearchRequest");
    }

    // Основные геттеры
    public List<Criteria> getSearchCriteria() { return searchCriteria; }
    public List<Sort> getSortCriteria() { return sortCriteria; }
    public Integer getPage() { return page; }
    public Integer getPageSize() { return pageSize; }
    public boolean isCountTotal() { return countTotal; }

    // Builder методы
    public SearchRequest addCriteria(Criteria criteria) {
        this.searchCriteria.add(criteria);
        return this;
    }

    public SearchRequest addSort(Sort sort) {
        this.sortCriteria.add(sort);
        return this;
    }

    public SearchRequest withPagination(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
        return this;
    }

    public SearchRequest withCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
        return this;
    }

    // Вспомогательные методы
    public boolean hasSearchCriteria() {
        return !searchCriteria.isEmpty();
    }

    public boolean hasSortCriteria() {
        return !sortCriteria.isEmpty();
    }

    public boolean hasPagination() {
        return page != null && pageSize != null;
    }
}