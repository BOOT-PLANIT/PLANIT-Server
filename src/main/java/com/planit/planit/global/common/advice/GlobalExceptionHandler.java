package com.planit.planit.global.common.advice;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.response.ErrorDetail;
import com.planit.planit.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.planit.planit.global.common.exception.ErrorCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        logWarning(e, errorCode.getStatus().value());
        // 예외 생성 시 전달된 메시지가 있으면 사용하고, 없으면 ErrorCode의 메시지를 사용
        return responseException(errorCode, e.getMessage(), null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logWarning(e, PARAMETER_NOT_FOUND.getStatus().value());
        return responseException(PARAMETER_NOT_FOUND, null, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logWarning(e, HttpStatus.BAD_REQUEST.value());
        return responseException(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ErrorDetail>>> handleMethodArgumentValidation(MethodArgumentNotValidException e) {
        List<ErrorDetail> errors = e.getBindingResult()
            .getFieldErrors().stream()
            .map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .toList();

        logWarning(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value());
        return responseException(METHOD_ARGUMENT_NOT_VALID, null, errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        logWarning(e, RESOURCE_NOT_FOUND.getStatus().value());
        return responseException(RESOURCE_NOT_FOUND, null, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logError(e, INTERNAL_SERVER_ERROR.getStatus().value());
        // 내부 서버 에러는 민감 정보 노출을 막기 위해 enum 메시지 사용
        return responseException(INTERNAL_SERVER_ERROR, null, null);
    }

    private <T> ResponseEntity<ApiResponse<T>> responseException(ErrorCode errorCode, String overrideMessage, T data) {
        String message = overrideMessage != null ? overrideMessage : errorCode.getMessage();
        return responseException(errorCode.getStatus(), message, data);
    }

    private <T> ResponseEntity<ApiResponse<T>> responseException(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.response(status, message, data);
        return ResponseEntity.status(status).body(response);
    }

    private void logWarning(Exception e, int errorCode) {
        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, e.getMessage());
    }

    private void logError(Exception e, int errorCode) {
        log.error(e.getMessage(), e);
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, e.getMessage());
    }
}