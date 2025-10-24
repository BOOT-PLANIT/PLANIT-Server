package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionBeforeBootcampStartException extends BaseException {

  public SessionBeforeBootcampStartException() {
    super(ErrorCode.SESSION_BEFORE_BOOTCAMP_START);
  }

  public SessionBeforeBootcampStartException(String message) {
    super(ErrorCode.SESSION_BEFORE_BOOTCAMP_START, message);
  }
}

