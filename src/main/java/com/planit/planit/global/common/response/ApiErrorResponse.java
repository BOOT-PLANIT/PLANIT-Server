package com.planit.planit.global.common.response;

import java.util.Objects;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ApiErrorResponse<E>(int code, String message, List<E> errors) {

    /** errors는 절대 null이 되지 않도록 List.of()로 초기화 */
    public static <E> ApiErrorResponse<E> of(HttpStatus status, String message) {
        Objects.requireNonNull(status, "HttpStatus must not be null");
        Objects.requireNonNull(message, "Message must not be null");
        return new ApiErrorResponse<>(status.value(), message, List.of());
    }

    /** 호출 측에서 null을 넘겨도 빈 리스트로 정규화 */
    public static <E> ApiErrorResponse<E> of(HttpStatus status, String message, List<E> errors) {
        Objects.requireNonNull(status, "HttpStatus must not be null");
        Objects.requireNonNull(message, "Message must not be null");
        return new ApiErrorResponse<>(
            status.value(),
            message,
            errors != null ? List.copyOf(errors) : List.of()
        );
    }
}