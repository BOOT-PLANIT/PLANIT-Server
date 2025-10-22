package com.planit.planit.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4XX
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 인자입니다."),
    PARAMETER_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청에 Parameter가 존재하지 않습니다."),
    PATH_VARIABLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "경로 변수가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터의 타입이 올바르지 않습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 RESOURCE, URL를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Media Type 입니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다."), // Rate Limiting 추후 필요 시
    CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다."),

    // 5XX
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server 내부 에러가 발생했습니다."),

    // Bootcamp Errors
    BOOTCAMP_NOT_FOUND(HttpStatus.NOT_FOUND, "부트캠프를 찾을 수 없습니다."),
    BOOTCAMP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 부트캠프입니다."),
    BOOTCAMP_INVALID_CLASS_DATES(HttpStatus.BAD_REQUEST, "유효하지 않은 교육일 형식입니다."),

    // Session Errors
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."),

    // UnitPeriod Errors
    UNIT_PERIOD_NOT_FOUND(HttpStatus.NOT_FOUND, "단위기간을 찾을 수 없습니다."),

	// User Errors
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
	DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;

    /**
     * 모든 메시지는 non-null을 보장
     * 핸들러에서의 null-safe 폴백은 방어적 코드로 유지
     */
    private final String message;
}
