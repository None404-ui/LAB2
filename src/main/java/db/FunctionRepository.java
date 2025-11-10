package db;

public interface FunctionRepository {
    boolean createFunction(String name, int userId, String expression);
    void getUserFunctions(int userId);
    void getFunctionById(int functionId);
    void getAllFunctions();
    boolean updateFunctionExpression(int functionId, String newExpression);
    boolean updateFunctionName(int functionId, String newName);
    boolean deleteFunction(int functionId);
    int getUserFunctionCount(int userId);
}