package com.planit.planit.global.common.response;

import org.springframework.http.HttpStatus;
import java.util.List;

public record ApiErrorResponse<E>(int code, String message, List<E> errors) {

    public static <E> ApiErrorResponse<E> of(HttpStatus status, String message) {
        return new ApiErrorResponse<>(status.value(), message, null);
    }

    public static <E> ApiErrorResponse<E> of(HttpStatus status, String message, List<E> errors) {
        return new ApiErrorResponse<>(status.value(), message, errors);
    }
}
