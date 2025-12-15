package com.planit.planit.domain.enrollment.controller;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDTO;
import com.planit.planit.domain.enrollment.service.MyBootcampService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bootcamps")
@RequiredArgsConstructor
@Tag(name = "사용자 부트캠프 등록", description = "사용자의 부트캠프 등록 관리 API")
public class MyBootcampController {

	private final MyBootcampService myBootcampService;

	@Operation(
		summary = "부트캠프 등록",
		description = "사용자가 특정 부트캠프에 등록합니다.",
		security = @SecurityRequirement(name = "BearerAuth"),
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "부트캠프 등록 성공",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증 필요",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "사용자 또는 부트캠프 없음",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "409",
				description = "이미 등록된 부트캠프",
				content = @Content
			)
		}
	)
	@PostMapping("/{bootcampId}/enroll")
	public ResponseEntity<ApiResponse<EnrollmentResponseDTO>> enrollBootcamp(
		@PathVariable Long bootcampId,
		Authentication auth
	) {
		String uid = auth.getName();
		EnrollmentResponseDTO response = myBootcampService.enrollBootcamp(uid, bootcampId);
		return ResponseEntity.ok(ApiResponse.success("부트캠프 등록 완료", response));
	}

	@Operation(
		summary = "내 부트캠프 목록 조회",
		description = "사용자가 등록한 부트캠프 목록을 조회합니다.",
		security = @SecurityRequirement(name = "BearerAuth"),
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "조회 성공",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증 필요",
				content = @Content
			)
		}
	)
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<MyBootcampDTO>>> getMyBootcamps(
		Authentication auth
	) {
		String uid = auth.getName();
		List<MyBootcampDTO> bootcamps = myBootcampService.getMyBootcampsByUid(uid);
		return ResponseEntity.ok(ApiResponse.success("내 부트캠프 목록 조회 성공", bootcamps));
	}

	@Operation(
		summary = "내 부트캠프 삭제",
		description = "사용자가 등록한 부트캠프를 삭제합니다.",
		security = @SecurityRequirement(name = "BearerAuth"),
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "204",
				description = "삭제 성공",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "401",
				description = "인증 필요",
				content = @Content
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "등록 정보를 찾을 수 없음",
				content = @Content
			)
		}
	)
	@DeleteMapping("/my/{enrollmentId}")
	public ResponseEntity<Void> deleteMyBootcamp(
		@PathVariable Long enrollmentId,
		Authentication auth
	) {
		String uid = auth.getName();
		myBootcampService.deleteMyBootcampByUid(enrollmentId, uid);
		return ResponseEntity.noContent().build();
	}
}
