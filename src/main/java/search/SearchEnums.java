package search;

public class SearchEnums {

    public enum SearchOperator {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_EQUAL(">="),
        LESS_THAN_EQUAL("<="),
        LIKE("LIKE"),
        IN("IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL");

        private final String sqlOperator;

        SearchOperator(String sqlOperator) {
            this.sqlOperator = sqlOperator;
        }

        public String getSqlOperator() {
            return sqlOperator;
        }

        public boolean requiresValue() {
            return this != IS_NULL && this != IS_NOT_NULL;
        }
    }

    public enum LogicalOperator {
        AND("AND"),
        OR("OR");

        private final String sqlOperator;

        LogicalOperator(String sqlOperator) {
            this.sqlOperator = sqlOperator;
        }

        public String getSqlOperator() {
            return sqlOperator;
        }
    }

    public enum SortDirection {
        ASC("ASC"),
        DESC("DESC");

        private final String sqlDirection;

        SortDirection(String sqlDirection) {
            this.sqlDirection = sqlDirection;
        }

        public String getSqlDirection() {
            return sqlDirection;
        }
    }
}