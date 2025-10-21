package com.planit.planit.domain.bootcamp.exception;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;

public class BootcampNotFoundException extends BaseException {
    
    public BootcampNotFoundException() {
        super(ErrorCode.BOOTCAMP_NOT_FOUND);
    }
    
    public BootcampNotFoundException(String message) {
        super(ErrorCode.BOOTCAMP_NOT_FOUND, message);
    }
}

