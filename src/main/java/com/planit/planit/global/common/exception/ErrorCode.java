package com.planit.planit.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4XX Errors
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 [인자]입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 [RESOURCE, URL]를 찾을 수 없습니다."),
    PARAMETER_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청에 [Parameter]가 존재하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    
    // Bootcamp Errors
    BOOTCAMP_NOT_FOUND(HttpStatus.NOT_FOUND, "부트캠프를 찾을 수 없습니다."),
    BOOTCAMP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 부트캠프입니다."),
    BOOTCAMP_INVALID_CLASS_DATES(HttpStatus.BAD_REQUEST, "유효하지 않은 교육일 형식입니다."),

    // 5XX Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[Server] 내부 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
