package com.planit.planit.domain.attendance.dto;

import java.time.OffsetDateTime;
import com.planit.planit.domain.attendance.enums.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttendanceDTO {

  @Schema(description = "출결 ID", example = "30")
  private Long id;

  @Schema(description = "사용자 ID", example = "5")
  private Long userId;

  @Schema(description = "세션 ID", example = "10")
  private Long sessionId;

  @Schema(description = "단위기간 ID", example = "2")
  private Long periodId;

  @Schema(description = "출결 상태", example = "present")
  private AttendanceStatus status;

  @Schema(description = "생성 시각")
  private OffsetDateTime createdAt;

  @Schema(description = "수정 시각")
  private OffsetDateTime updatedAt;

  // id 없이 쓰고 싶을 때를 위한 편의 생성자
  public AttendanceDTO(Long userId, Long sessionId, Long periodId, AttendanceStatus status) {
    this.userId = userId;
    this.sessionId = sessionId;
    this.periodId = periodId;
    this.status = status;
  }
}
