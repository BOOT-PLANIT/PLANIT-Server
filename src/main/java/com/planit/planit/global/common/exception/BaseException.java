package com.planit.planit.global.common.exception;

import org.springframework.http.HttpStatus;

/**
 * BaseException은 ErrorCode를 보관
 * - 기본 메시지는 ErrorCode의 메시지를 사용하고,
 * - 필요 시 오버라이드 메시지를 생성자에 전달할 수 있음
 */
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}
