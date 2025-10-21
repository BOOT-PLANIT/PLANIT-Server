package com.planit.planit.global.common.response;

public record ErrorDetail(
    String errorField,
    String errorMessage,
    Object inputValue
) {
    public static ErrorDetail of(String errorField, String errorMessage, Object inputValue) {
        return new ErrorDetail(errorField, errorMessage, inputValue);
    }
}