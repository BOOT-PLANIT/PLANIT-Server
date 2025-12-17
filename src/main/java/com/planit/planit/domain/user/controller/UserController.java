package com.planit.planit.domain.user.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.planit.planit.domain.user.dto.MeResponseDTO;
import com.planit.planit.domain.user.dto.SaveFcmTokenRequestDTO;
import com.planit.planit.domain.user.mapper.UserMapper;
import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.domain.user.model.UserLevel;
import com.planit.planit.domain.user.service.FirebaseAccountService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
	private final FirebaseAccountService firebaseAccountService;

	@Operation(
		summary = "내 정보 조회",
		security = { @SecurityRequirement(name = "BearerAuth") },
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "조회 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = MeResponseDTO.class),
					examples = @ExampleObject(
						name = "success",
						value = """
                    {
                      "code": 200,
                      "message": "내 정보",
                      "data": {
                        "id": 2,
                        "uid": "QaaPkMvoi8dylCeO6jrewrGsFx62",
                        "email": "xhdrp@sookmyung.ac.kr",
                        "displayName": "지연",
                        "photoUrl": "https://lh3.googleusercontent.com/a/xxx=s96-c",
                        "userLevel": "USER",
                        "provider": "google.com",
                        "emailVerified": true,
                        "createdAt": "2025-10-24T14:40:33",
                        "lastLoginAt": "2025-10-24T14:40:33",
                        "recentBootcampId": 1
                      }
                    }
                    """
					)
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증이 필요합니다.",
				content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
						name = "unauthorized",
						value = """
                    {
                      "code": 401,
                      "message": "인증이 필요합니다.",
                      "errors": []
                    }
                    """
					)
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "사용자 없음",
				content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
						name = "not-found",
						value = """
                    {
                      "code": 404,
                      "message": "사용자를 찾을 수 없습니다.",
                      "errors": []
                    }
                    """
					)
				)
			)
		}
	)
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MeResponseDTO>> me(Authentication auth) {
		if (auth == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		String uid = auth.getName();
		var user = userMapper.findByUid(uid)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
		Long recentBootcampId = firebaseAccountService.findRecentBootcampId(user.getId());

		MeResponseDTO meResponseDTO = MeResponseDTO.builder()
			.id(user.getId())
			.uid(user.getUid())
			.email(user.getEmail())
			.displayName(user.getDisplayName())
			.photoUrl(user.getPhotoUrl())
			.userLevel(user.getUserLevel())
			.provider(user.getProvider())
			.emailVerified(user.isEmailVerified())
			.createdAt(user.getCreatedAt())
			.lastLoginAt(user.getLastLoginAt())
			.recentBootcampId(recentBootcampId)
			.build();
		return ResponseEntity.ok(ApiResponse.success("내 정보", meResponseDTO));
	}

	@Operation(
		summary = "회원 탈퇴",
		security = { @SecurityRequirement(name = "BearerAuth") },
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "204",
				description = "탈퇴 성공",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증이 필요합니다.",
				content = @Content(
					mediaType = "application/json"
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "이미 탈퇴했거나 존재하지 않는 사용자",
				content = @Content(
					mediaType = "application/json"
				)
			)
		}
	)
	@DeleteMapping("/me")
	@Transactional
	public ResponseEntity<Void> deleteMe(Authentication auth) throws Exception {
		String uid = auth.getName();

		int updated = userMapper.softDeleteUser(uid);
		if (updated == 0) {
			ProblemDetail body = ProblemDetail.forStatusAndDetail(
				HttpStatus.NOT_FOUND, "이미 탈퇴했거나 존재하지 않는 사용자입니다."
			);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// 토큰 무효화 후 계정 삭제
		firebaseAuth.revokeRefreshTokens(uid);
		firebaseAuth.deleteUser(uid);

		return ResponseEntity.noContent().build(); // 204
	}

	@Operation(
		summary = "FCM 토큰 저장/갱신",
		security = { @SecurityRequirement(name = "BearerAuth") },
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 저장 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "fcmToken 누락 또는 잘못된 값"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
		}
	)
	@Transactional
	@PostMapping("/me/token")
	public ResponseEntity<Void> saveFcmToken(
		Authentication auth,
		@Valid @RequestBody SaveFcmTokenRequestDTO request) {

		String uid = auth.getName();
		String fcmToken = request.getFcmToken();

		// DB 업데이트
		int updated = userMapper.updateFcmToken(uid, fcmToken);

		if (updated == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
		}

		return ResponseEntity.ok().build();
	}
}
