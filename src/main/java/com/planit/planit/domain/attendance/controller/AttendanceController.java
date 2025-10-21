package com.planit.planit.domain.attendance.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.planit.planit.domain.attendance.dto.AttendanceDTO;
import com.planit.planit.domain.attendance.dto.AttendanceDailyResponseDto;
import com.planit.planit.domain.attendance.service.AttendanceService;
import com.planit.planit.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/attendance")
@Tag(name = "출결", description = "출결 관리 API")
public class AttendanceController {

  private AttendanceService service;

  public AttendanceController(AttendanceService service) {
    this.service = service;
  }

  @Operation(summary = "일단위 출결 조회", description = "선택한 일자의 출결 정보를 조회합니다.",
      responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "출결 조회 성공",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = AttendanceDailyResponseDto.class)))})
  @GetMapping("/{userId}")
  public ApiResponse<?> getDailyAttendance(
      @Parameter(description = "조회할 사용자 ID", example = "1") @PathVariable("userId") long userId,

      @Parameter(description = "조회할 날짜",
          example = "2025-09-01") @RequestParam(value = "date") String date,

      @Parameter(description = "조회할 부프캠프 ID",
          example = "1") @RequestParam(value = "bootcampId") long bootcampId) {
    AttendanceDailyResponseDto daily = service.getDaily(userId, bootcampId, date);

    if (daily == null) {
      // 해당일자 출결정보가 없으면 등록화면으로
      return ApiResponse.success(date + " 출결 등록 가능", true);

    } else {
      // 출결정보가 있으면 보여주고, 수정화면으로
      return ApiResponse.success(date + " 출결 조회", daily);
    }

  }

  @Operation(summary = "일단위 출결 등록", description = "선택한 일자의 출결 정보를 등록합니다..",
      responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "출결 등록 성공")})
  @PostMapping
  public ApiResponse<String> AttendanceRegist(
      @RequestBody @Schema(implementation = AttendanceDTO.class) AttendanceDTO attendance) {
    service.regist(attendance);
    return ApiResponse.success("출결 등록 성공");
  }

  @Operation(summary = "일단위 출결 수정", description = "선택한 일자의 출결 정보를 수정합니다..",
      responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "출결 수정 성공")})
  @PatchMapping
  public ApiResponse<String> AttendanceUpdate(
      @RequestBody @Schema(implementation = AttendanceDTO.class) AttendanceDTO attendance) {
    service.update(attendance);
    return ApiResponse.success("출결 수정 성공");
  }
}
