package com.planit.planit.global.common.advice;

import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.exception.UnauthorizedException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    @RestController
    @RequestMapping("/test")
    public static class TestController {
        @GetMapping("/unauthorized")
        public void unauthorized() { throw new UnauthorizedException("custom unauthorized"); }

        @GetMapping("/require-param")
        public String requireParam(@RequestParam String id) { return id; }

        @PostMapping("/echo")
        public Object echo(@RequestBody Object body) { return body; }
    }

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void handleBaseException_shouldReturnStatusFromException() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getStatus().value()))
            .andExpect(jsonPath("$.message").value("custom unauthorized"))
            .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void missingServletRequestParameter_shouldReturnParameterNotFound() throws Exception {
        mockMvc.perform(get("/test/require-param"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(ErrorCode.PARAMETER_NOT_FOUND.getStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.PARAMETER_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }
}
