package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DifferentiateRequest {
    @JsonProperty("functionId")
    private Integer functionId;
    
    @JsonProperty("resultName")
    private String resultName;
    
    @JsonProperty("factoryType")
    private String factoryType; // "ARRAY" или "LINKED_LIST"

    public DifferentiateRequest() {}

    public DifferentiateRequest(Integer functionId, String resultName, String factoryType) {
        this.functionId = functionId;
        this.resultName = resultName;
        this.factoryType = factoryType;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
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



