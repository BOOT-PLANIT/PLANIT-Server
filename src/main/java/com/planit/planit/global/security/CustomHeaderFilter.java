package com.planit.planit.global.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomHeaderFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletResponse res = (HttpServletResponse) response;

		// Content-Type charset 강제
		res.setCharacterEncoding("UTF-8");

		// 캐시 제어
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);

		// 보안 헤더
		res.setHeader("X-Frame-Options", "DENY");
		res.setHeader("X-XSS-Protection", "1; mode=block");
		res.setHeader("X-Content-Type-Options", "nosniff");

		chain.doFilter(request, response);
	}
}
