package com.planit.planit.global.common.response;

import java.io.Serializable;

public record ErrorDetail(
    String errorField,
    String errorMessage,
    Object inputValue
) implements Serializable {
    public static ErrorDetail of(String errorField, String errorMessage, Object inputValue) {
        return new ErrorDetail(errorField, errorMessage, inputValue);
    }
}
