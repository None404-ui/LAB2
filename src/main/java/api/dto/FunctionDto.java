package api.dto;

import java.time.LocalDateTime;

public class FunctionDto {
    private Integer functionId;
    private String name;
    private String expression;
    private Integer userId;
    private LocalDateTime createdAt;

    public FunctionDto() {}

    public FunctionDto(Integer functionId, String name, String expression, Integer userId, LocalDateTime createdAt) {
        this.functionId = functionId;
        this.name = name;
        this.expression = expression;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


