package API.controller;

import API.dtoForApi.*;
import API.service.MathService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MathFunctionsController {
    private final MathService mathService = new MathService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //USERS
    public String getUsers(int page, int size) throws Exception {
        List<UserResponse> users = mathService.getUsers(page, size);
        ApiResponse<List<UserResponse>> response = ApiResponse.success(users);
        return objectMapper.writeValueAsString(response);
    }

    public String createUser(String requestBody) throws Exception {
        UserRequest request = objectMapper.readValue(requestBody, UserRequest.class);
        UserResponse user = mathService.createUser(request);
        ApiResponse<UserResponse> response = ApiResponse.success(user);
        return objectMapper.writeValueAsString(response);
    }

    //FUNCTIONS
    public String getFunctions(int page, int size) throws Exception {
        List<FunctionResponse> functions = mathService.getFunctions(page, size);
        ApiResponse<List<FunctionResponse>> response = ApiResponse.success(functions);
        return objectMapper.writeValueAsString(response);
    }

    public String createFunction(String requestBody) throws Exception {
        FunctionRequest request = objectMapper.readValue(requestBody, FunctionRequest.class);
        FunctionResponse function = mathService.createFunction(request);
        ApiResponse<FunctionResponse> response = ApiResponse.success(function);
        return objectMapper.writeValueAsString(response);
    }

    //POINTS
    public String getPoints(int page, int size) throws Exception {
        List<PointResponse> points = mathService.getPoints(page, size);
        ApiResponse<List<PointResponse>> response = ApiResponse.success(points);
        return objectMapper.writeValueAsString(response);
    }

    public String createPoint(String requestBody) throws Exception {
        PointRequest request = objectMapper.readValue(requestBody, PointRequest.class);
        PointResponse point = mathService.createPoint(request);
        ApiResponse<PointResponse> response = ApiResponse.success(point);
        return objectMapper.writeValueAsString(response);
    }

    public String getUserFunctions(Integer userId) throws Exception {
        List<FunctionResponse> functions = mathService.getUserFunctions(userId);
        ApiResponse<List<FunctionResponse>> response = ApiResponse.success(functions);
        return objectMapper.writeValueAsString(response);
    }

    public String getFunctionPoints(Integer functionId) throws Exception {
        List<PointResponse> points = mathService.getFunctionPoints(functionId);
        ApiResponse<List<PointResponse>> response = ApiResponse.success(points);
        return objectMapper.writeValueAsString(response);
    }
}