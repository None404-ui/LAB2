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

    public List<UserResponse> getUsers(int page, int size) {
        return users;
    }

    public UserResponse createUser(UserRequest request) {
        UserResponse user = new UserResponse();
        user.setUserId(nextUserId++);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        users.add(user);
        return user;
    }

    public List<FunctionResponse> getFunctions(int page, int size) {
        return functions;
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

    public List<PointResponse> getPoints(int page, int size) {
        return points;
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