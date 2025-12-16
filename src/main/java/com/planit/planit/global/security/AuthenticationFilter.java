package com.planit.planit.global.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

	private static final String SESSION_COOKIE_NAME = "planit_session";

	private final ObjectProvider<FirebaseAccountService> accountServiceProvider;
	private final FirebaseAuth firebaseAuth;

	@Override
	protected void doFilterInternal(
		HttpServletRequest req,
		HttpServletResponse res,
		FilterChain chain
	) throws ServletException, IOException {

		// 이미 인증된 경우 스킵
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			chain.doFilter(req, res);
			return;
		}

		// Session Cookie 추출
		String sessionCookie = resolveSessionCookie(req);
		if (sessionCookie == null) {
			chain.doFilter(req, res);
			return;
		}

		try {
			// Session Cookie 검증
			FirebaseToken decoded = firebaseAuth.verifySessionCookie(sessionCookie);

			// UserDetails 로드
			FirebaseAccountService accountService = accountServiceProvider.getObject();
			UserDetails user = accountService.ensureAndLoad(decoded);

			// 인증 객체 생성
			var authentication = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
			);

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (com.google.firebase.auth.FirebaseAuthException e) {
			log.warn(
				"[AUTH] verifySessionCookie failed code={} msg={}",
				e.getAuthErrorCode(),
				e.getMessage(),
				e
			);
			SecurityContextHolder.clearContext();

		} catch (Exception e) {
			Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
			log.warn(
				"[AUTH] authentication failed: {}, root={}",
				e.getMessage(),
				root.getMessage(),
				e
			);
			SecurityContextHolder.clearContext();
		}

		chain.doFilter(req, res);
	}

	/**
	 * planit_session 쿠키 추출
	 */
	private String resolveSessionCookie(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}

		for (Cookie cookie : request.getCookies()) {
			if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}
}

