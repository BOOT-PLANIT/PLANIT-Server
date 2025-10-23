package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionIsBootcampStartDateException extends BaseException {

  public SessionIsBootcampStartDateException() {
    super(ErrorCode.SESSION_IS_BOOTCAMP_START_DATE);
  }

  public SessionIsBootcampStartDateException(String message) {
    super(ErrorCode.SESSION_IS_BOOTCAMP_START_DATE, message);
  }
}

