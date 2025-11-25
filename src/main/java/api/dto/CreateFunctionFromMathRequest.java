package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateFunctionFromMathRequest {
    private String name;
    private String mathFunctionType;
    
    @JsonProperty("xFrom")
    private double xFrom;
    
    @JsonProperty("xTo")
    private double xTo;
    
    private int count;
    private String factoryType;

    public CreateFunctionFromMathRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMathFunctionType() {
        return mathFunctionType;
    }

    public void setMathFunctionType(String mathFunctionType) {
        this.mathFunctionType = mathFunctionType;
    }

    public double getXFrom() {
        return xFrom;
    }

    @JsonProperty("xFrom")
    public void setXFrom(double xFrom) {
        this.xFrom = xFrom;
    }

    public double getXTo() {
        return xTo;
    }

    @JsonProperty("xTo")
    public void setXTo(double xTo) {
        this.xTo = xTo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }
    
    @Override
    public String toString() {
        return "CreateFunctionFromMathRequest{" +
                "name='" + name + '\'' +
                ", mathFunctionType='" + mathFunctionType + '\'' +
                ", xFrom=" + xFrom +
                ", xTo=" + xTo +
                ", count=" + count +
                ", factoryType='" + factoryType + '\'' +
                '}';
    }
}



