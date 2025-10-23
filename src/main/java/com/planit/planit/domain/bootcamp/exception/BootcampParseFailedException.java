package com.planit.planit.domain.bootcamp.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class BootcampParseFailedException extends BaseException {

  public BootcampParseFailedException() {
    super(ErrorCode.BOOTCAMP_PARSE_FAILED);
  }

  public BootcampParseFailedException(String message) {
    super(ErrorCode.BOOTCAMP_PARSE_FAILED, message);
  }
}

