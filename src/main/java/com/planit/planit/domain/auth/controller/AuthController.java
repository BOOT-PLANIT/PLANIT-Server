package com.planit.planit.domain.auth.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import com.planit.planit.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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
		@RequestHeader("Authorization") String authorization
	) throws Exception {
		String idToken = extractBearer(authorization);
		FirebaseToken token = firebaseAuth.verifyIdToken(idToken);
		accountService.ensureAndLoad(token);

		return ResponseEntity.ok(ApiResponse.success("로그인 성공", null));
	}

	private String extractBearer(String header) {
		if (header == null || !header.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization 헤더가 유효하지 않아요");
		}
		return header.substring(7);
	}
}

