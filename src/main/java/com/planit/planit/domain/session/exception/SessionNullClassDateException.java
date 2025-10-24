package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionNullClassDateException extends BaseException {

    public SessionNullClassDateException() {
        super(ErrorCode.SESSION_NULL_CLASS_DATE);
    }

    public SessionNullClassDateException(String message) {
        super(ErrorCode.SESSION_NULL_CLASS_DATE, message);
    }
}
