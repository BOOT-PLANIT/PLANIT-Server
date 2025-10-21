package com.planit.planit.global.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> response(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus.value(), message, data);
    }

    public static <T> ApiResponse<T> response(HttpStatus httpStatus, String message) {
        return new ApiResponse<>(httpStatus.value(), message, null);
    }
}
