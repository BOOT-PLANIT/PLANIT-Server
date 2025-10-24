package com.planit.planit.global.common.advice;

import com.planit.planit.global.common.exception.BaseException;
import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.response.ApiErrorResponse;
import com.planit.planit.global.common.response.ErrorDetail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.BindException;

import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;


import java.util.List;

import static com.planit.planit.global.common.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String DEFAULT_SERVER_MESSAGE = "서버 오류가 발생했습니다.";

	/** 도메인 정의 예외 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleBaseException(BaseException e) {
		ErrorCode error = e.getErrorCode();
		String msg = (e.getMessage() != null) ? e.getMessage()
			: (error.getMessage() != null ? error.getMessage() : DEFAULT_SERVER_MESSAGE);
		logWarn(e, error.getStatus().value(), msg);
		return ResponseEntity.status(error.getStatus())
			.body(ApiErrorResponse.of(error.getStatus(), msg));
	}

	/** @RequestParam 누락 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
		String msg = PARAMETER_NOT_FOUND.getMessage();
		logWarn(e, PARAMETER_NOT_FOUND.getStatus().value(), msg);
		return ResponseEntity.status(PARAMETER_NOT_FOUND.getStatus())
			.body(ApiErrorResponse.of(PARAMETER_NOT_FOUND.getStatus(), msg));
	}

	/** @PathVariable 누락 -> 전용 코드 사용 */
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMissingPathVariable(MissingPathVariableException e) {
		String msg = PATH_VARIABLE_NOT_FOUND.getMessage();
		logWarn(e, PATH_VARIABLE_NOT_FOUND.getStatus().value(), msg);
		return ResponseEntity.status(PATH_VARIABLE_NOT_FOUND.getStatus())
			.body(ApiErrorResponse.of(PATH_VARIABLE_NOT_FOUND.getStatus(), msg));
	}

	/** JSON 파싱/역직렬화 실패 -> ErrorCode 일관 사용 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		String msg = INVALID_REQUEST_BODY.getMessage();
		logWarn(e, INVALID_REQUEST_BODY.getStatus().value(), msg);
		return ResponseEntity.status(INVALID_REQUEST_BODY.getStatus())
			.body(ApiErrorResponse.of(INVALID_REQUEST_BODY.getStatus(), msg));
	}

	/** @Valid @RequestBody 필드 단위 검증 실패 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		return handleBindingResultErrors(e, e.getBindingResult());
	}

	/** @Validated @ModelAttribute 바인딩/검증 실패 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleBindException(BindException e) {
		return handleBindingResultErrors(e, e.getBindingResult());
	}

	// 공통 처리 추출 (DRY)
	private ResponseEntity<ApiErrorResponse<ErrorDetail>> handleBindingResultErrors(
		Exception e, BindingResult bindingResult
	) {
		List<ErrorDetail> errors = bindingResult.getFieldErrors().stream()
			.map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
			.toList();
		String msg = METHOD_ARGUMENT_NOT_VALID.getMessage();
		logWarn(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value(), msg);
		return ResponseEntity.status(METHOD_ARGUMENT_NOT_VALID.getStatus())
			.body(ApiErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getStatus(), msg, errors));
	}

	/** @RequestParam/@PathVariable 타입 불일치 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
		String reqType = (e.getRequiredType() != null) ? e.getRequiredType().getSimpleName() : null;
		String typeInfo = (reqType != null) ? String.format(" (필요 타입: %s)", reqType) : "";
		ErrorDetail detail = ErrorDetail.of(
			e.getName(),
			String.format("요청 파라미터 타입이 올바르지 않습니다.%s", typeInfo),
			e.getValue()
		);
		String msg = TYPE_MISMATCH.getMessage();
		logWarn(e, TYPE_MISMATCH.getStatus().value(), msg);
		return ResponseEntity.status(TYPE_MISMATCH.getStatus())
			.body(ApiErrorResponse.of(TYPE_MISMATCH.getStatus(), msg, List.of(detail)));
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
		String msg = TYPE_MISMATCH.getMessage();
		logWarn(e, TYPE_MISMATCH.getStatus().value(), msg);
		return ResponseEntity.status(TYPE_MISMATCH.getStatus())
			.body(ApiErrorResponse.of(TYPE_MISMATCH.getStatus(), msg, List.of(detail)));
	}

	/** 메서드 파라미터 제약(@Min, @Pattern 등) 위반 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleConstraintViolation(ConstraintViolationException e) {
		List<ErrorDetail> errors = e.getConstraintViolations().stream()
			.map(this::toErrorDetail)
			.toList();
		String msg = METHOD_ARGUMENT_NOT_VALID.getMessage();
		logWarn(e, METHOD_ARGUMENT_NOT_VALID.getStatus().value(), msg);
		return ResponseEntity.status(METHOD_ARGUMENT_NOT_VALID.getStatus())
			.body(ApiErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getStatus(), msg, errors));
	}

	/** 404 - 스프링 정적 리소스/핸들러 미발견 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleNoResourceFound(NoResourceFoundException e) {
		String msg = RESOURCE_NOT_FOUND.getMessage();
		logWarn(e, RESOURCE_NOT_FOUND.getStatus().value(), msg);
		return ResponseEntity.status(RESOURCE_NOT_FOUND.getStatus())
			.body(ApiErrorResponse.of(RESOURCE_NOT_FOUND.getStatus(), msg));
	}

	/** 405 - 지원하지 않는 메서드 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		String msg = METHOD_NOT_ALLOWED.getMessage();
		logWarn(e, METHOD_NOT_ALLOWED.getStatus().value(), msg);
		return ResponseEntity.status(METHOD_NOT_ALLOWED.getStatus())
			.body(ApiErrorResponse.of(METHOD_NOT_ALLOWED.getStatus(), msg));
	}

	/** 409 - DB/유니크 제약 위반 등 */
	@ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleConflict(Exception e) {
		// 세션 중복 날짜 특화 처리
		if (e.getMessage() != null && e.getMessage().contains("sessions") && 
			(e.getMessage().contains("bootcamp_id") || e.getMessage().contains("class_date"))) {
			String msg = SESSION_DUPLICATE_DATE.getMessage();
			logWarn(e, SESSION_DUPLICATE_DATE.getStatus().value(), msg);
			return ResponseEntity.status(SESSION_DUPLICATE_DATE.getStatus())
				.body(ApiErrorResponse.of(SESSION_DUPLICATE_DATE.getStatus(), msg));
		}
		
		String msg = CONFLICT.getMessage();
		logWarn(e, CONFLICT.getStatus().value(), msg);
		return ResponseEntity.status(CONFLICT.getStatus())
			.body(ApiErrorResponse.of(CONFLICT.getStatus(), msg));
	}

	/** 415 - 미지원 미디어 타입 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
		String msg = UNSUPPORTED_MEDIA_TYPE.getMessage();
		logWarn(e, UNSUPPORTED_MEDIA_TYPE.getStatus().value(), msg);
		return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE.getStatus())
			.body(ApiErrorResponse.of(UNSUPPORTED_MEDIA_TYPE.getStatus(), msg));
	}

	/** 최종 캐치 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse<ErrorDetail>> handleException(Exception e) {
		String msg = INTERNAL_SERVER_ERROR.getMessage();
		logError(e, INTERNAL_SERVER_ERROR.getStatus().value(), msg);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiErrorResponse.of(INTERNAL_SERVER_ERROR.getStatus(), msg));
	}

	private ErrorDetail toErrorDetail(ConstraintViolation<?> v) {
		String field = v.getPropertyPath().toString();
		return ErrorDetail.of(field, v.getMessage(), v.getInvalidValue());
	}

	/** 로깅 */
	private void logWarn(Exception e, int code, String msg) {
		log.warn("Class: {}, Code: {}, Message: {}", e.getClass().getSimpleName(), code, msg, e);
	}
	private void logError(Exception e, int code, String msg) {
		log.error("Class: {}, Code: {}, Message: {}", e.getClass().getSimpleName(), code, msg, e);
	}
}
