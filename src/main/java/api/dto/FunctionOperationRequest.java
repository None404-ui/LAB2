package api.dto;

public class FunctionOperationRequest {
    private Integer functionId1;
    private Integer functionId2;
    private String operation; // "ADD", "SUBTRACT", "MULTIPLY", "DIVIDE"
    private String resultName;
    private String factoryType; // "ARRAY" или "LINKED_LIST"

    public FunctionOperationRequest() {}

    public FunctionOperationRequest(Integer functionId1, Integer functionId2, String operation, String resultName, String factoryType) {
        this.functionId1 = functionId1;
        this.functionId2 = functionId2;
        this.operation = operation;
        this.resultName = resultName;
        this.factoryType = factoryType;
    }

    public Integer getFunctionId1() {
        return functionId1;
    }

    public void setFunctionId1(Integer functionId1) {
        this.functionId1 = functionId1;
    }

    public Integer getFunctionId2() {
        return functionId2;
    }

    public void setFunctionId2(Integer functionId2) {
        this.functionId2 = functionId2;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }
}



