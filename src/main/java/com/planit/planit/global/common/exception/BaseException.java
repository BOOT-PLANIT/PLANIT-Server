package com.planit.planit.global.common.exception;

import org.springframework.http.HttpStatus;

/**
 * ErrorCode를 보관하는 실행 예외
 * - 기본 메시지는 ErrorCode의 메시지를 사용
 * - 필요 시 생성자에 커스텀 메시지를 전달해 오버라이드
 */
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    /** ErrorCode의 기본 메시지를 사용하는 생성자 */
    public BaseException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /** 커스텀 메시지로 ErrorCode 기본 메시지를 오버라이드하는 생성자 */
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
