package com.planit.planit.domain.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttendanceDailyResponseDTO {
  @Schema(description = "출결 ID", example = "1")
  private Long attendanceId;

  @Schema(description = "출결 상태", example = "present")
  private String status;

  @Schema(description = "부트캠프 ID", example = "1")
  private Long bootcampId;

  @Schema(description = "부트캠프 이름", example = "AI Bootcamp")
  private String bootcampName;

  @Schema(description = "세션 ID", example = "1")
  private Long sessionId;

  @Schema(description = "수업 날짜", example = "2025-09-01")
  private String classDate;

  @Schema(description = "단위기간 번호", example = "1")
  private Integer unitNo;

  @Schema(description = "단위기간 시작일", example = "2025-09-01")
  private String startDate;

  @Schema(description = "단위기간 종료일", example = "2025-09-30")
  private String endDate;
}
