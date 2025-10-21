package com.planit.planit.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4XX
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 [인자]입니다."),
    PARAMETER_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청에 [Parameter]가 존재하지 않습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터의 타입이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 [RESOURCE, URL]를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Media Type 입니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다."),
    CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다."),

    // 5XX
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "[Server] 내부 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
