package com.planit.planit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.response.ApiErrorResponse;
import jakarta.servlet.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class Json403AccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper om = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res,
		AccessDeniedException ex) throws IOException {
		var code = ErrorCode.FORBIDDEN;
		res.setStatus(code.getStatus().value());
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json;charset=UTF-8");
		om.writeValue(res.getWriter(), ApiErrorResponse.of(code.getStatus(), code.getMessage()));
	}
}
