package com.planit.planit.domain.auth.controller;

import com.planit.planit.domain.auth.service.AuthService;
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
@Tag(name = "Auth", description = "로그인/로그아웃 API")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(
		@RequestHeader("Authorization") String authorization,
		HttpServletResponse response
	) {
		String idToken = extractBearer(authorization);

		String sessionCookie = authService.login(idToken);

		ResponseCookie cookie = ResponseCookie.from("planit_session", sessionCookie)
			.httpOnly(true)
			.secure(false) // 배포 시 true
			.sameSite("None")
			.path("/")
			.maxAge(Duration.ofDays(1))
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(ApiResponse.success("로그인 성공", null));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("planit_session", "")
			.httpOnly(true)
			.secure(false) // 배포 시 true
			.sameSite("None")
			.path("/")
			.maxAge(0)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
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
}
