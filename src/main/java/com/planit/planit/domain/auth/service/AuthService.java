package com.planit.planit.domain.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.planit.planit.domain.auth.exception.AuthenticationException;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final FirebaseAuth firebaseAuth;
	private final FirebaseAccountService accountService;

	/**
	 * 로그인 처리
	 * - ID Token 검증
	 * - 최근 로그인 여부 확인
	 * - 유저 생성/조회
	 * - Session Cookie 발급
	 */
	public String login(String idToken) {
		try {
			FirebaseToken token = firebaseAuth.verifyIdToken(idToken);

			validateRecentLogin(token);

			accountService.ensureAndLoad(token);

			long expiresInMs = Duration.ofDays(1).toMillis();

			return firebaseAuth.createSessionCookie(
				idToken,
				SessionCookieOptions.builder()
					.setExpiresIn(expiresInMs)
					.build()
			);

		} catch (FirebaseAuthException e) {
			log.warn("[AUTH] login failed code={} msg={}",
				e.getAuthErrorCode(), e.getMessage(), e);

			throw new AuthenticationException("유효하지 않은 인증 토큰입니다");
		}
	}

	/**
	 * 로그아웃 처리
	 * - Session Cookie 검증
	 * - 글로벌 세션에는 영향 없음
	 */
	public void logout() {
  // no-op
	}

	/**
	 * 최근 로그인 여부 검증 (5분 이내)
	 */
	private void validateRecentLogin(FirebaseToken token) {
		Object authTimeObj = token.getClaims().get("auth_time");

		if (!(authTimeObj instanceof Number authTime)) {
			throw new AuthenticationException("auth_time claim missing");
		}

		long nowSeconds = System.currentTimeMillis() / 1000;
		if (nowSeconds - authTime.longValue() > 5 * 60) {
			throw new AuthenticationException("recent login required");
		}
	}
}
