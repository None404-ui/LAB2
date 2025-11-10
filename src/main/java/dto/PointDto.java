package dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

public class PointDto {
    private static final Logger logger = LoggerFactory.getLogger(PointDto.class);

    private Integer pointId;
    private Integer functionId;
    private Double xValue;
    private Double yValue;
    private LocalDateTime computedAt;

    // Конструкторы
    public PointDto() {
        logger.debug("Creating empty PointDto");
    }

    public PointDto(Integer functionId, Double xValue, Double yValue) {
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
        logger.debug("Creating PointDto for functionId: {}, x: {}", functionId, xValue);
    }

    // Геттеры и сеттеры
    public Integer getPointId() {
        logger.trace("Getting pointId: {}", pointId);
        return pointId;
    }

    public void setPointId(Integer pointId) {
        logger.debug("Setting pointId from {} to {}", this.pointId, pointId);
        this.pointId = pointId;
    }

    public Integer getFunctionId() {
        logger.trace("Getting functionId: {}", functionId);
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        logger.debug("Setting functionId from {} to {}", this.functionId, functionId);
        this.functionId = functionId;
    }

    public Double getXValue() {
        logger.trace("Getting xValue: {}", xValue);
        return xValue;
    }

    public void setXValue(Double xValue) {
        logger.debug("Setting xValue from {} to {}", this.xValue, xValue);
        this.xValue = xValue;
    }

    public Double getYValue() {
        logger.trace("Getting yValue: {}", yValue);
        return yValue;
    }

    public void setYValue(Double yValue) {
        logger.debug("Setting yValue from {} to {}", this.yValue, yValue);
        this.yValue = yValue;
    }

    public LocalDateTime getComputedAt() {
        logger.trace("Getting computedAt: {}", computedAt);
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        logger.debug("Setting computedAt from {} to {}", this.computedAt, computedAt);
        this.computedAt = computedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            logger.trace("PointDto equals: same object");
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            logger.trace("PointDto equals: different classes or null");
            return false;
        }
        PointDto pointDto = (PointDto) o;
        boolean isEqual = Objects.equals(pointId, pointDto.pointId) &&
                Objects.equals(functionId, pointDto.functionId) &&
                Objects.equals(xValue, pointDto.xValue);
        logger.debug("PointDto equals result: {} for pointId {}", isEqual, pointId);
        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(pointId, functionId, xValue);
        logger.trace("PointDto hashCode: {} for pointId {}", hash, pointId);
        return hash;
    }

    @Override
    public String toString() {
        String str = "PointDto{" +
                "pointId=" + pointId +
                ", functionId=" + functionId +
                ", xValue=" + xValue +
                ", yValue=" + yValue +
                '}';
        logger.trace("PointDto toString: {}", str);
        return str;
    }
}