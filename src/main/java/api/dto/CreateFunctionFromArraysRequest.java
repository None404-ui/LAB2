package api.dto;

import java.util.Arrays;

public class CreateFunctionFromArraysRequest {
    private String name;
    private double[] xValues;
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

    public void setXValues(double[] xValues) {
        this.xValues = xValues;
    }

    public double[] getYValues() {
        return yValues;
    }

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



