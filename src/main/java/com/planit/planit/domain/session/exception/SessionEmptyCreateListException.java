package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionEmptyCreateListException extends BaseException {

    public SessionEmptyCreateListException() {
        super(ErrorCode.SESSION_EMPTY_CREATE_LIST);
    }

    public SessionEmptyCreateListException(String message) {
        super(ErrorCode.SESSION_EMPTY_CREATE_LIST, message);
    }
}
