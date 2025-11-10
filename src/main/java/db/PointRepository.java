package db;

public interface PointRepository {
    boolean createPoint(int functionId, double x, double y);
    void getFunctionPoints(int functionId);
    void getPointsInRange(int functionId, double minX, double maxX);
    boolean updatePointY(int pointId, double newY);
    boolean deletePoint(int pointId);
    boolean deletePointsByFunction(int functionId);
    int getPointCount(int functionId);
}