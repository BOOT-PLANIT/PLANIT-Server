package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionEmptyDeleteListException extends BaseException {

    public SessionEmptyDeleteListException() {
        super(ErrorCode.SESSION_EMPTY_DELETE_LIST);
    }

    public SessionEmptyDeleteListException(String message) {
        super(ErrorCode.SESSION_EMPTY_DELETE_LIST, message);
    }
}
