package api.dto;

public class CreateFunctionRequest {
    private String name;
    private String expression;
    private Integer userId;

    public CreateFunctionRequest() {}

    public CreateFunctionRequest(String name, String expression, Integer userId) {
        this.name = name;
        this.expression = expression;
        this.userId = userId;
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
}


