package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionNotFoundException extends BaseException {

    public SessionNotFoundException() {
        super(ErrorCode.SESSION_NOT_FOUND);
    }

    public SessionNotFoundException(String message) {
        super(ErrorCode.SESSION_NOT_FOUND, message);
    }
}
