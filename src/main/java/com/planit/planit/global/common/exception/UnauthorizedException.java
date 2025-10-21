package com.planit.planit.global.common.exception;

/**
 * 인증되지 않은 접근 시 발생하는 예외 - ErrorCode.UNAUTHORIZED 사용
 */
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
