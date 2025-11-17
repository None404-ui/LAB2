package API.dtoForApi;



public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;


    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;

    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getError() { return error; }


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> response = new ApiResponse<>(false, null, null);
        response.error = error;
        return response;
    }
}