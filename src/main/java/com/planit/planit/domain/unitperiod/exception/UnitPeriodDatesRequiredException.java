package com.planit.planit.domain.unitperiod.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class UnitPeriodDatesRequiredException extends BaseException {

  public UnitPeriodDatesRequiredException() {
    super(ErrorCode.UNIT_PERIOD_DATES_REQUIRED);
  }

  public UnitPeriodDatesRequiredException(String message) {
    super(ErrorCode.UNIT_PERIOD_DATES_REQUIRED, message);
  }
}

