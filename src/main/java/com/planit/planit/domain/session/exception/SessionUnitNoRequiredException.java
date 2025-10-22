package com.planit.planit.domain.session.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class SessionUnitNoRequiredException extends BaseException {

  public SessionUnitNoRequiredException() {
    super(ErrorCode.SESSION_UNIT_NO_REQUIRED);
  }

  public SessionUnitNoRequiredException(String message) {
    super(ErrorCode.SESSION_UNIT_NO_REQUIRED, message);
  }
}

