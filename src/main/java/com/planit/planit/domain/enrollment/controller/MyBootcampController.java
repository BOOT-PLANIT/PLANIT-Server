package com.planit.planit.domain.enrollment.controller;

import com.planit.planit.domain.enrollment.dto.MyBootcampDto;
import com.planit.planit.domain.enrollment.service.MyBootcampService;
import com.planit.planit.global.common.response.ApiResponse;
import com.planit.planit.global.common.response.ErrorDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bootcamps")
@RequiredArgsConstructor
@Tag(name = "사용자 부트캠프 등록", description = "사용자의 부트캠프 등록 관리 API")
public class MyBootcampController {

	private final MyBootcampService myBootcampService;

	/** 부트캠프 등록 */
	@Operation(summary = "부트캠프 등록", description = "특정 사용자가 부트캠프에 등록(참여)합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
				description = "등록 성공",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = VoidResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
				description = "잘못된 요청 또는 이미 등록됨",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
				description = "사용자 또는 부트캠프를 찾을 수 없음",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
				description = "서버 에러",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class)))
		})
	@PostMapping("/{bootcampId}/enroll")
	public ResponseEntity<ApiResponse<Void>> enrollBootcamp(
		@Parameter(description = "등록할 부트캠프의 ID") @PathVariable Long bootcampId,
		@Parameter(description = "등록 요청을 하는 사용자의 ID") @RequestParam Long userId
	) {
		myBootcampService.enrollBootcamp(userId, bootcampId);
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "부트캠프 등록 완료", null));
	}

	/** 내 부트캠프 목록 보기 */
	@Operation(summary = "내 부트캠프 목록 조회", description = "특정 사용자가 등록한 모든 부트캠프 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
				description = "조회 성공",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = MyBootcampListResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
				description = "사용자를 찾을 수 없음",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
				description = "서버 에러",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class)))
		})
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<MyBootcampDto>>> getMyBootcamps(
		@Parameter(description = "목록을 조회할 사용자의 ID") @RequestParam Long userId
	) {
		List<MyBootcampDto> bootcamps = myBootcampService.getMyBootcamps(userId);
		return ResponseEntity.ok(ApiResponse.success("내 부트캠프 목록 조회 성공", bootcamps));
	}

	/** 내 부트캠프 삭제 */
	@Operation(summary = "내 부트캠프 등록 해제", description = "특정 사용자가 등록했던 부트캠프 등록을 해제(삭제)합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
				description = "삭제 성공",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = VoidResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
				description = "사용자 또는 등록 정보를 찾을 수 없음",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
				description = "서버 에러",
				content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
					schema = @Schema(implementation = ApiErrorResponseSchema.class)))
		})
	@DeleteMapping("/{bootcampId}")
	public ResponseEntity<ApiResponse<Void>> deleteMyBootcamp(
		@Parameter(description = "등록 해제할 부트캠프의 ID") @PathVariable Long bootcampId,
		@Parameter(description = "등록 해제를 요청하는 사용자의 ID") @RequestParam Long userId
	) {
		myBootcampService.deleteMyBootcamp(userId, bootcampId);
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "부트캠프 삭제 완료", null));
	}

	@Schema(name = "MyBootcampListResponse", description = "내 부트캠프 목록 응답")
	static class MyBootcampListResponseSchema {
		@Schema(example = "200")
		public int code;
		@Schema(example = "내 부트캠프 목록 조회 성공")
		public String message;
		public List<MyBootcampDto> data;
	}

	@Schema(name = "VoidResponse", description = "데이터 없는 성공 응답")
	static class VoidResponseSchema {
		@Schema(example = "200")
		public int code;
		@Schema(example = "부트캠프 등록 완료")
		public String message;
		public Void data;
	}

	@Schema(name = "ApiErrorResponse", description = "실패 응답(에러)")
	static class ApiErrorResponseSchema {
		@Schema(example = "400")
		public int code;
		@Schema(example = "잘못된 [인자]입니다.")
		public String message;
		public List<ErrorDetail> errors;
	}
}
