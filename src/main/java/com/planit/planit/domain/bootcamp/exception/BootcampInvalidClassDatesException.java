package com.planit.planit.domain.bootcamp.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class BootcampInvalidClassDatesException extends BaseException {

  public BootcampInvalidClassDatesException() {
    super(ErrorCode.BOOTCAMP_INVALID_CLASS_DATES);
  }

  public BootcampInvalidClassDatesException(String message) {
    super(ErrorCode.BOOTCAMP_INVALID_CLASS_DATES, message);
  }
}

