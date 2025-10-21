package com.planit.planit.domain.unitperiod.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class UnitPeriodNotFoundException extends BaseException {

  public UnitPeriodNotFoundException() {
    super(ErrorCode.UNIT_PERIOD_NOT_FOUND);
  }

  public UnitPeriodNotFoundException(String message) {
    super(ErrorCode.UNIT_PERIOD_NOT_FOUND, message);
  }
}

