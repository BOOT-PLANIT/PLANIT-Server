package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionDuplicateDateException extends BaseException {

    public SessionDuplicateDateException() {
        super(ErrorCode.SESSION_DUPLICATE_DATE);
    }

    public SessionDuplicateDateException(String message) {
        super(ErrorCode.SESSION_DUPLICATE_DATE, message);
    }
}
