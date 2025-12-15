package com.planit.planit.domain.enrollment.controller;

import com.planit.planit.domain.enrollment.dto.EnrollmentResponseDTO;
import com.planit.planit.domain.enrollment.dto.MyBootcampDto;
import com.planit.planit.domain.enrollment.service.MyBootcampService;
import com.planit.planit.global.common.response.ApiResponse;
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

	/** 부트캠프 등록 */
	@PostMapping("/{bootcampId}/enroll")
	public ResponseEntity<ApiResponse<EnrollmentResponseDTO>> enrollBootcamp(
		@PathVariable Long bootcampId,
		Authentication auth
	) {
		String uid = auth.getName();
		EnrollmentResponseDTO response = myBootcampService.enrollBootcamp(uid, bootcampId);

		return ResponseEntity.ok(ApiResponse.success("부트캠프 등록 완료", response));
	}

	/** 내 부트캠프 목록 */
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<MyBootcampDto>>> getMyBootcamps(
		Authentication auth
	) {
		String uid = auth.getName();
		List<MyBootcampDto> bootcamps = myBootcampService.getMyBootcampsByUid(uid);

		return ResponseEntity.ok(ApiResponse.success("내 부트캠프 목록 조회 성공", bootcamps));
	}

	/** 내 부트캠프 삭제 */
	@DeleteMapping("/my/{enrollmentId}")
	public ResponseEntity<ApiResponse<Void>> deleteMyBootcamp(
		@PathVariable Long enrollmentId,
		Authentication auth
	) {
		String uid = auth.getName();
		myBootcampService.deleteMyBootcampByUid(enrollmentId, uid);

		return ResponseEntity.ok(ApiResponse.success("부트캠프 삭제 완료", null));
	}
}

