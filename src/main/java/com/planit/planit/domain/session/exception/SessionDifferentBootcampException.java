package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionDifferentBootcampException extends BaseException {

    public SessionDifferentBootcampException() {
        super(ErrorCode.SESSION_DIFFERENT_BOOTCAMP);
    }

    public SessionDifferentBootcampException(String message) {
        super(ErrorCode.SESSION_DIFFERENT_BOOTCAMP, message);
    }
}
