package com.planit.planit.global.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.user.service.FirebaseAccountService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private final ObjectProvider<FirebaseAccountService> accountServiceProvider;
	private final FirebaseAuth firebaseAuth;

	/**
	 * 인증이 필요 없는 요청은 필터 자체를 실행하지 않고 패스
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();

		return uri.startsWith("/swagger-ui")
			|| uri.startsWith("/v3/api-docs")
			|| uri.startsWith("/swagger-resources")
			|| uri.startsWith("/webjars")
			|| uri.equals("/favicon.ico")
			|| uri.equals("/")
			|| uri.equals("/index.html")
			|| uri.startsWith("/api/v1/auth")  // 로그인/회원가입 API는 인증 제외
			|| uri.startsWith("/error");       // Spring 기본 error path
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		throws ServletException, IOException {

		String tokenStr = resolveBearer(req.getHeader("Authorization"));

		if (tokenStr != null) {
			try {
				FirebaseToken decoded = firebaseAuth.verifyIdToken(tokenStr);

				FirebaseAccountService accountService = accountServiceProvider.getObject();
				UserDetails user = accountService.ensureAndLoad(decoded);

				var auth = new UsernamePasswordAuthenticationToken(
					user,
					null,
					user.getAuthorities()
				);

				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (com.google.firebase.auth.FirebaseAuthException e) {
				log.warn("[AUTH] Firebase verify failed :: code={} msg={} cause={}",
					e.getAuthErrorCode(),
					e.getMessage(),
					(e.getCause() != null ? e.getCause().getMessage() : "n/a"),
					e
				);
				SecurityContextHolder.clearContext();

			} catch (Exception e) {
				Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
				log.warn("[AUTH] general auth failure :: {} (root: {})",
					e, root.getMessage(), e);
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(req, res);
	}

	private String resolveBearer(String header) {
		if (!StringUtils.hasText(header)) return null;
		if (!header.startsWith("Bearer ")) return null;
		return header.substring(7).trim();
	}
}
