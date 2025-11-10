package db.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Function {
    private final int functionId;
    private final String name;
    private final int userId;
    private final String expression;
    private final Timestamp createdAt;

    public Function(int functionId, String name, int userId, String expression, Timestamp createdAt) {
        this.functionId = functionId;
        this.name = name;
        this.userId = userId;
        this.expression = expression;
        this.createdAt = createdAt;
    }

    // Getters
    public int getFunctionId() { return functionId; }
    public String getName() { return name; }
    public int getUserId() { return userId; }
    public String getExpression() { return expression; }
    public Timestamp getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return functionId == function.functionId && Objects.equals(name, function.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionId, name);
    }

    @Override
    public String toString() {
        return String.format("Function{id=%d, name='%s', expression='%s'}", functionId, name, expression);
    }
}