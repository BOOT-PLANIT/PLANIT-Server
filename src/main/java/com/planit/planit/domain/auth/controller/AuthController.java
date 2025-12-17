package com.planit.planit.domain.auth.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인 API는 프론트에서 진행해야 합니다")
public class AuthController {

	private final FirebaseAuth firebaseAuth;
	private final FirebaseAccountService accountService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(
		@RequestHeader("Authorization") String authorization,
		HttpServletResponse httpServletResponse
	) throws FirebaseAuthException {

		// Bearer 토큰 추출
		String idToken = extractBearer(authorization);

		// Firebase ID Token 검증
		FirebaseToken token = firebaseAuth.verifyIdToken(idToken);

		// 최근 로그인(auth_time) 검증
		validateRecentLogin(token);

		// 유저 생성/조회 + UserDetails 로드
		var userDetails = accountService.ensureAndLoad(token);

		// Firebase Session Cookie 생성 (1일)
		long expiresInMs = Duration.ofDays(1).toMillis();

		SessionCookieOptions options =
			SessionCookieOptions.builder()
				.setExpiresIn(expiresInMs)
				.build();

		String sessionCookie =
			firebaseAuth.createSessionCookie(idToken, options);

		// 운영환경에서는 secure=true 필수
		boolean isProd = false; // HTTP 요청에서도 쿠키 전송 가능

		ResponseCookie cookie = ResponseCookie.from("planit_session", sessionCookie)
			.httpOnly(true)
			.secure(isProd)
			.sameSite("Lax")
			.path("/")
			.maxAge(Duration.ofMillis(expiresInMs))
			.build();

		httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(
			ApiResponse.success("로그인 성공",null)
		);
	}

	/**
	 * Authorization 헤더에서 Bearer 토큰 추출
	 */
	private String extractBearer(String header) {
		if (header == null || !header.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization 헤더가 유효하지 않아요");
		}
		return header.substring(7);
	}

	/**
	 * 최근 로그인 여부 검증 (5분 이내)
	 */
	private void validateRecentLogin(FirebaseToken token) {
		Object authTimeObj = token.getClaims().get("auth_time");

		if (!(authTimeObj instanceof Number authTime)) {
			throw new IllegalArgumentException("auth_time claim missing");
		}

		long nowSeconds = System.currentTimeMillis() / 1000;
		if (nowSeconds - authTime.longValue() > 5 * 60) {
			throw new IllegalArgumentException("recent login required");
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@CookieValue(name = "planit_session", required = false) String sessionCookie,
		HttpServletResponse response
	) throws Exception {
		if (sessionCookie != null) {
			FirebaseToken decoded = firebaseAuth.verifySessionCookie(sessionCookie);
			firebaseAuth.revokeRefreshTokens(decoded.getUid());
		}
		// planit_session 쿠키 만료
		ResponseCookie cookie = ResponseCookie.from("planit_session", "")
			.httpOnly(true)
			.secure(false) // prod에서는 true
			.sameSite("Lax")
			.path("/")
			.maxAge(0) // 즉시 만료
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(
			ApiResponse.success("로그아웃 성공", null)
		);
	}
}
