package com.planit.planit.domain.session.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "세션과 출결 상태 조회 DTO")
public class SessionWithAttendanceDTO {
  @Schema(description = "세션 ID", example = "1")
  private Long sessionId;

  @Schema(description = "부트캠프 ID", example = "1")
  private Long bootcampId;

  @Schema(description = "단위기간 ID", example = "1")
  private Long periodId;

  @Schema(description = "단위기간 번호", example = "1")
  private Integer unitNo;

  @Schema(description = "수업 날짜", example = "2025-01-15")
  private LocalDate classDate;

  @Schema(description = "출결 상태", example = "PRESENT", allowableValues = {"PRESENT", "ABSENT", "LATE", "EXCUSED"})
  private String attendanceStatus;
}
