package com.planit.planit.global.common.advice;

import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.exception.UnauthorizedException;
import com.planit.planit.global.common.response.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * - 성공: ApiResponse
 * - 실패: ApiErrorResponse<ErrorDetail> (errors는 항상 빈 배열 [])
 */
class GlobalExceptionHandlerTest {

    @RestController
    @RequestMapping("/test")
    @Validated // 메서드 파라미터 검증용
    public static class TestController {

        /** BaseException -> UNAUTHORIZED(401) */
        @GetMapping("/unauthorized")
        public void unauthorized() { throw new UnauthorizedException("custom unauthorized"); }

        /** @RequestParam 누락 -> PARAMETER_NOT_FOUND(400) */
        @GetMapping("/require-param")
        public String requireParam(@RequestParam String id) { return id; }

        /** 타입 불일치 -> TYPE_MISMATCH(400) */
        @GetMapping("/type-mismatch")
        public String typeMismatch(@RequestParam Long num) { return String.valueOf(num); }

        /** @Valid @RequestBody 검증 실패 -> METHOD_ARGUMENT_NOT_VALID(400) */
        @PostMapping(value = "/validate-body", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ApiResponse<SimpleDto> validateBody(@Valid @RequestBody SimpleDto body) {
            return ApiResponse.success("ok", body);
        }

        /** @ModelAttribute 바인딩/검증 실패 -> METHOD_ARGUMENT_NOT_VALID(400) */
        @GetMapping("/validate-model")
        public ApiResponse<SimpleDto> validateModel(@Valid SimpleDto dto) {
            return ApiResponse.success("ok", dto);
        }

        /** JSON 파싱 실패 -> INVALID_REQUEST_BODY(400) */
        @PostMapping(value = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
        public Object echo(@RequestBody Object body) { return body; }

        /** 미지원 미디어 타입 -> UNSUPPORTED_MEDIA_TYPE(415) */
        @PostMapping(value = "/json-only", consumes = MediaType.APPLICATION_JSON_VALUE)
        public String jsonOnly(@RequestBody String body) { return body; }

        /** DB 제약 위반 -> CONFLICT(409) */
        @GetMapping("/conflict-di")
        public void conflictDI() { throw new DataIntegrityViolationException("duplicate"); }

        /** 중복 키 -> CONFLICT(409) */
        @GetMapping("/conflict-dup")
        public void conflictDup() { throw new DuplicateKeyException("duplicate"); }
    }

    @Getter @Setter @NoArgsConstructor
    public static class SimpleDto {
        @NotBlank(message = "name은 비어 있을 수 없습니다.")
        private String name;

        @Pattern(regexp = "^[a-z0-9]{4,12}$", message = "username은 소문자/숫자 4~12자")
        private String username;

        @Min(value = 10, message = "limit는 10 이상이어야 합니다.")
        private Integer limit;
    }

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // standaloneSetup에서는 Validator를 직접 주입해야 @Valid가 동작
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void baseException_shouldReturn401() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getStatus().value()))
            .andExpect(jsonPath("$.message").value("custom unauthorized"))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void missingServletRequestParameter_shouldReturn400() throws Exception {
        mockMvc.perform(get("/test/require-param"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.PARAMETER_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.PARAMETER_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void typeMismatch_shouldReturn400_withDetail() throws Exception {
        mockMvc.perform(get("/test/type-mismatch").param("num", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.TYPE_MISMATCH.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.TYPE_MISMATCH.getMessage()))
            .andExpect(jsonPath("$.errors[0].errorField").value("num"))
            .andExpect(jsonPath("$.errors[0].inputValue").value("abc"));
    }

    @Test
    void methodArgumentNotValid_shouldReturn400() throws Exception {
        String badJson = """
        { "name": "", "username": "AA" }
        """;
        mockMvc.perform(post("/test/validate-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage()))
            .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void bindException_shouldReturn400() throws Exception {
        // name 누락 + username 형식 위반 + limit 제약 위반
        mockMvc.perform(get("/test/validate-model").param("username", "A!").param("limit", "5"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage()))
            .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void httpMessageNotReadable_shouldReturn400() throws Exception {
        String invalidJson = "{ invalid json ";
        mockMvc.perform(post("/test/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST_BODY.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST_BODY.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void methodNotAllowed_shouldReturn405() throws Exception {
        mockMvc.perform(put("/test/unauthorized"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void unsupportedMediaType_shouldReturn415() throws Exception {
        mockMvc.perform(post("/test/json-only")
                .contentType(MediaType.TEXT_PLAIN) // JSON만 받도록 선언된 곳에 text/plain 전송
                .content("plain"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void dataIntegrityViolation_shouldReturn409() throws Exception {
        mockMvc.perform(get("/test/conflict-di"))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.CONFLICT.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.CONFLICT.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

    @Test
    void duplicateKey_shouldReturn409() throws Exception {
        mockMvc.perform(get("/test/conflict-dup"))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.CONFLICT.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.CONFLICT.getMessage()))
            .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }
}
