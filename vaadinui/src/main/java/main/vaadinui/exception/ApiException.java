package main.vaadinui.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int statusCode;

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public static ApiException unauthorized() {
        return new ApiException("Unauthorized", 401);
    }

    public static ApiException forbidden() {
        return new ApiException("Forbidden", 403);
    }

    public static ApiException notFound(String message) {
        return new ApiException(message, 404);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(message, 400);
    }

    public static ApiException serverError(String message) {
        return new ApiException(message, 500);
    }
}