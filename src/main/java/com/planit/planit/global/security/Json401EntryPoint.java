package com.planit.planit.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.planit.global.common.exception.ErrorCode;
import com.planit.planit.global.common.response.ApiErrorResponse;
import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class Json401EntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper om = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest req, HttpServletResponse res,
		AuthenticationException ex) throws IOException {
		var code = ErrorCode.UNAUTHORIZED;
		res.setStatus(code.getStatus().value());
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json;charset=UTF-8");
		om.writeValue(res.getWriter(), ApiErrorResponse.of(code.getStatus(), code.getMessage()));
	}
}
