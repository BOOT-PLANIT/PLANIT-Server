package com.planit.planit.domain.user.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.planit.planit.domain.user.mapper.UserMapper;
import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "사용자 계정 관리 API")
public class UserController {

	private final FirebaseAuth firebaseAuth;
	private final UserMapper userMapper;

	@Operation(
		summary = "내 정보 조회",
		security = { @SecurityRequirement(name = "BearerAuth") }
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserAccount>> me(Authentication auth) {
		String uid = auth.getName();
		var user = userMapper.findByUid(uid)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
		return ResponseEntity.ok(ApiResponse.success("내 정보", user));
	}

	@Operation(
		summary = "회원 탈퇴",
		security = { @SecurityRequirement(name = "BearerAuth") }
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 성공")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "탈퇴 실패")
	@DeleteMapping("/me")
	@Transactional
	public ResponseEntity<?> deleteMe(Authentication auth) throws Exception {
		String uid = auth.getName();

		int updated = userMapper.softDeleteUser(uid);
		if (updated == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "이미 탈퇴했거나 존재하지 않는 사용자입니다."));
		}
		firebaseAuth.revokeRefreshTokens(uid);
		firebaseAuth.deleteUser(uid);

		return ResponseEntity.noContent().build();
	}
}
