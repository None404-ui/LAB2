package api.dto;

public class CreateFunctionFromMathRequest {
    private String name;
    private String mathFunctionType; // "SQR", "IDENTITY", "CONSTANT", "UNIT", "ZERO"
    private double xFrom;
    private double xTo;
    private int count;
    private String factoryType; // "ARRAY" или "LINKED_LIST"

    public CreateFunctionFromMathRequest() {}

    public CreateFunctionFromMathRequest(String name, String mathFunctionType, double xFrom, double xTo, int count, String factoryType) {
        this.name = name;
        this.mathFunctionType = mathFunctionType;
        this.xFrom = xFrom;
        this.xTo = xTo;
        this.count = count;
        this.factoryType = factoryType;
    }

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

    public void setXFrom(double xFrom) {
        this.xFrom = xFrom;
    }

    public double getXTo() {
        return xTo;
    }

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
}



