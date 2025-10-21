package com.planit.planit.global.common.response;

import java.io.Serializable;

public record ErrorDetail(
    String errorField,
    String errorMessage,
    Object inputValue
) implements Serializable {
    public static ErrorDetail of(String errorField, String errorMessage, Object inputValue) {
        Object safeValue = maskSensitiveField(errorField, inputValue);
        return new ErrorDetail(errorField, errorMessage, safeValue);
    }

    private static Object maskSensitiveField(String fieldName, Object value) {
        if (value == null) return null;
        if (fieldName.toLowerCase().contains("password")
            || fieldName.toLowerCase().contains("token")
            || fieldName.toLowerCase().contains("secret")) {
            return "***";
        }
        return value;
    }
}
