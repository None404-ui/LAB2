package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public class CreateFunctionFromArraysRequest {
    private String name;
    
    @JsonProperty("xValues")
    private double[] xValues;
    
    @JsonProperty("yValues")
    private double[] yValues;
    
    private String factoryType;

    public CreateFunctionFromArraysRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getXValues() {
        return xValues;
    }

    @JsonProperty("xValues")
    public void setXValues(double[] xValues) {
        this.xValues = xValues;
    }

    public double[] getYValues() {
        return yValues;
    }

    @JsonProperty("yValues")
    public void setYValues(double[] yValues) {
        this.yValues = yValues;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }
    
    @Override
    public String toString() {
        return "CreateFunctionFromArraysRequest{" +
                "name='" + name + '\'' +
                ", xValues=" + Arrays.toString(xValues) +
                ", yValues=" + Arrays.toString(yValues) +
                ", factoryType='" + factoryType + '\'' +
                '}';
    }
}



