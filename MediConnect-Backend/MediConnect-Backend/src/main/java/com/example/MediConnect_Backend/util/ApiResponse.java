package com.example.MediConnect_Backend.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;


@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String timestamp;
    private final T data;
    private final ErrorDetails error;

    private ApiResponse(T data) {
        this.success = true;
        this.timestamp = Instant.now().toString();
        this.data = data;
        this.error = null;
    }

    private ApiResponse(int status, String message, String path) {
        this.success = false;
        this.timestamp = Instant.now().toString();
        this.data = null;
        this.error = new ErrorDetails(status, message, path);
    }

    private ApiResponse(String message) {
        this.success = false;
        this.timestamp = Instant.now().toString();
        this.data = null;
        this.error = new ErrorDetails(400, message, null);
    }


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }


    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(status, message, path);
    }


    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message);
    }


    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }


    @Getter
    private static class ErrorDetails {
        private final int status;
        private final String message;
        private final String path;

        ErrorDetails(int status, String message, String path) {
            this.status = status;
            this.message = message;
            this.path = path;
        }
    }
}