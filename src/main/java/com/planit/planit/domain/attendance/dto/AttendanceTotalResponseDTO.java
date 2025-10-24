package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class AttendanceTotalResponseDTO {
  private Long userId;
  private Integer unitNo; // 단위기간 조회용 (전체기간이면 null 가능)
  private Integer presentCount;
  private Integer absentCount;
  private Integer lateCount;
  private Integer leftEarlyCount;
  private Integer annualCount;
  private Integer leaveCount;
  private Integer totalSessions;

}
