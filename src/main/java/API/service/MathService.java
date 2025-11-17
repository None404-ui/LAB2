package API.service;

import API.dtoForApi.*;
import java.util.ArrayList;
import java.util.List;

public class MathService {
    private List<UserResponse> users = new ArrayList<>();
    private List<FunctionResponse> functions = new ArrayList<>();
    private List<PointResponse> points = new ArrayList<>();
    private int nextUserId = 1;
    private int nextFunctionId = 1;
    private int nextPointId = 1;

    // ========== USER METHODS ==========
    public List<UserResponse> getUsers(int page, int size) {
        return users;
    }

    public UserResponse getUserById(int id) {
        for (UserResponse user : users) {
            if (user.getUserId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public UserResponse createUser(UserRequest request) {
        UserResponse user = new UserResponse();
        user.setUserId(nextUserId++);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        users.add(user);
        return user;
    }

    public boolean updateUser(int id, UserRequest request) {
        for (UserResponse user : users) {
            if (user.getUserId().equals(id)) {
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(int id) {
        return users.removeIf(user -> user.getUserId().equals(id));
    }

    // ========== FUNCTION METHODS ==========
    public List<FunctionResponse> getFunctions(int page, int size) {
        return functions;
    }

    public FunctionResponse getFunctionById(int id) {
        for (FunctionResponse function : functions) {
            if (function.getFunctionId().equals(id)) {
                return function;
            }
        }
        return null;
    }

    public FunctionResponse createFunction(FunctionRequest request) {
        FunctionResponse function = new FunctionResponse();
        function.setFunctionId(nextFunctionId++);
        function.setName(request.getName());
        function.setExpression(request.getExpression());
        function.setUserId(request.getUserId());
        functions.add(function);
        return function;
    }

    public boolean updateFunction(int id, FunctionRequest request) {
        for (FunctionResponse function : functions) {
            if (function.getFunctionId().equals(id)) {
                function.setName(request.getName());
                function.setExpression(request.getExpression());
                return true;
            }
        }
        return false;
    }

    public boolean deleteFunction(int id) {
        // Также удаляем все точки этой функции
        points.removeIf(point -> point.getFunctionId().equals(id));
        return functions.removeIf(function -> function.getFunctionId().equals(id));
    }

    // ========== POINT METHODS ==========
    public List<PointResponse> getPoints(int page, int size) {
        return points;
    }

    public PointResponse getPointById(int id) {
        for (PointResponse point : points) {
            if (point.getPointId().equals(id)) {
                return point;
            }
        }
        return null;
    }

    public PointResponse createPoint(PointRequest request) {
        PointResponse point = new PointResponse();
        point.setPointId(nextPointId++);
        point.setFunctionId(request.getFunctionId());
        point.setXValue(request.getXValue());
        point.setYValue(request.getYValue());
        points.add(point);
        return point;
    }

    public boolean updatePoint(int id, PointRequest request) {
        for (PointResponse point : points) {
            if (point.getPointId().equals(id)) {
                point.setXValue(request.getXValue());
                point.setYValue(request.getYValue());
                return true;
            }
        }
        return false;
    }

    public boolean deletePoint(int id) {
        return points.removeIf(point -> point.getPointId().equals(id));
    }

    // ========== RELATIONSHIP METHODS ==========
    public List<FunctionResponse> getUserFunctions(Integer userId) {
        List<FunctionResponse> result = new ArrayList<>();
        for (FunctionResponse function : functions) {
            if (function.getUserId().equals(userId)) {
                result.add(function);
            }
        }
        return result;
    }

    public List<PointResponse> getFunctionPoints(Integer functionId) {
        List<PointResponse> result = new ArrayList<>();
        for (PointResponse point : points) {
            if (point.getFunctionId().equals(functionId)) {
                result.add(point);
            }
        }
        return result;
    }
}