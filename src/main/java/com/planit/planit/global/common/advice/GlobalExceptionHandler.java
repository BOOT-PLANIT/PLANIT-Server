package com.planit.planit.global.common.advice;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.response.ApiErrorResponse;
import com.planit.planit.global.common.response.ErrorDetail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.BindException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.planit.planit.global.common.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    /** 도메인 정의 예외 */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleBaseException(BaseException e) {
        ErrorCode error = e.getErrorCode();
        logWarn(e, error.getStatus().value());
        String msg = (e.getMessage() != null) ? e.getMessage() : error.getMessage();
        return ResponseEntity.status(error.getStatus())
            .body(ApiErrorResponse.of(error.getStatus(), msg));
    }

    /** @RequestParam 누락 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        logWarn(e, PARAMETER_NOT_FOUND.getStatus().value());
        return ResponseEntity.status(PARAMETER_NOT_FOUND.getStatus())
            .body(ApiErrorResponse.of(PARAMETER_NOT_FOUND.getStatus(), PARAMETER_NOT_FOUND.getMessage()));
    }

    /** @PathVariable 누락 */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMissingPathVariable(MissingPathVariableException e) {
        logWarn(e, PARAMETER_NOT_FOUND.getStatus().value());
        return ResponseEntity.status(PARAMETER_NOT_FOUND.getStatus())
            .body(ApiErrorResponse.of(PARAMETER_NOT_FOUND.getStatus(), PARAMETER_NOT_FOUND.getMessage()));
    }

    /** JSON 파싱/역직렬화 실패 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        logWarn(e, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."));
    }

    /** @Valid @RequestBody 필드 단위 검증 실패 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ErrorDetail> errors = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .toList();
        logWarn(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value());
        return ResponseEntity.status(METHOD_ARGUMENT_NOT_VALID.getStatus())
            .body(ApiErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getStatus(), METHOD_ARGUMENT_NOT_VALID.getMessage(), errors));
    }

    /** @Validated @ModelAttribute 바인딩/검증 실패 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleBindException(BindException e) {
        List<ErrorDetail> errors = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .toList();
        logWarn(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value());
        return ResponseEntity.status(METHOD_ARGUMENT_NOT_VALID.getStatus())
            .body(ApiErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getStatus(), METHOD_ARGUMENT_NOT_VALID.getMessage(), errors));
    }

    /** @RequestParam/@PathVariable 타입 불일치 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {

        ErrorDetail detail = ErrorDetail.of(
            e.getName(),
            "요청 파라미터 타입이 올바르지 않습니다."
                + (e.getRequiredType() != null ? " (필요 타입: " + e.getRequiredType().getSimpleName() + ")" : ""),
            e.getValue()
        );

        logWarn(e, TYPE_MISMATCH.getStatus().value());
        return ResponseEntity.status(TYPE_MISMATCH.getStatus())
            .body(ApiErrorResponse.of(TYPE_MISMATCH.getStatus(), TYPE_MISMATCH.getMessage(), List.of(detail)));
    }

    /** 바인딩 전반(TypeConverter)에서의 타입 불일치 */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleSpringTypeMismatch(TypeMismatchException e) {

        ErrorDetail detail = ErrorDetail.of(
            e.getPropertyName(),
            "요청 값의 타입이 올바르지 않습니다."
                + (e.getRequiredType() != null ? " (필요 타입: " + e.getRequiredType().getSimpleName() + ")" : ""),
            e.getValue()
        );

        logWarn(e, TYPE_MISMATCH.getStatus().value());
        return ResponseEntity.status(TYPE_MISMATCH.getStatus())
            .body(ApiErrorResponse.of(TYPE_MISMATCH.getStatus(), TYPE_MISMATCH.getMessage(), List.of(detail)));
    }

    /** 메서드 파라미터 제약(@Min, @Pattern 등) 위반 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleConstraintViolation(ConstraintViolationException e) {
        List<ErrorDetail> errors = e.getConstraintViolations().stream()
            .map(this::toErrorDetail)
            .toList();
        logWarn(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value());
        return ResponseEntity.status(METHOD_ARGUMENT_NOT_VALID.getStatus())
            .body(ApiErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getStatus(), METHOD_ARGUMENT_NOT_VALID.getMessage(), errors));
    }

    /** 404 - 스프링 정적 리소스/핸들러 미발견 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleNoResourceFound(NoResourceFoundException e) {
        logWarn(e, RESOURCE_NOT_FOUND.getStatus().value());
        return ResponseEntity.status(RESOURCE_NOT_FOUND.getStatus())
            .body(ApiErrorResponse.of(RESOURCE_NOT_FOUND.getStatus(), RESOURCE_NOT_FOUND.getMessage()));
    }

    /** 405 - 지원하지 않는 메서드 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        logWarn(e, METHOD_NOT_ALLOWED.getStatus().value());
        return ResponseEntity.status(METHOD_NOT_ALLOWED.getStatus())
            .body(ApiErrorResponse.of(METHOD_NOT_ALLOWED.getStatus(), METHOD_NOT_ALLOWED.getMessage()));
    }

    /** 415 - 미지원 미디어 타입 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        logWarn(e, UNSUPPORTED_MEDIA_TYPE.getStatus().value());
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE.getStatus())
            .body(ApiErrorResponse.of(UNSUPPORTED_MEDIA_TYPE.getStatus(), UNSUPPORTED_MEDIA_TYPE.getMessage()));
    }

    /** 최종 캐치 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleException(Exception e) {
        logError(e, INTERNAL_SERVER_ERROR.getStatus().value());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
            .body(ApiErrorResponse.of(INTERNAL_SERVER_ERROR.getStatus(), INTERNAL_SERVER_ERROR.getMessage()));
    }

    private ErrorDetail toErrorDetail(ConstraintViolation<?> v) {
        String field = (v.getPropertyPath() != null) ? v.getPropertyPath().toString() : null;
        return ErrorDetail.of(field, v.getMessage(), v.getInvalidValue());
    }

    private void logWarn(Exception e, int code) {
        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), code, e.getMessage());
    }

    private void logError(Exception e, int code) {
        log.error(e.getMessage(), e);
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), code, e.getMessage());
    }
}
