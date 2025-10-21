package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class AttendanceDailyResponseDto {

  private Long attendanceId; // 출결 고유 ID
  private Long userId; // 사용자 ID
  private String username; // 사용자 이름
  private Long bootcampId; // 부트캠프 ID
  private String bootcampName; // 부트캠프 이름
  private Long periodId; // 단위기간 ID
  private String classDate; // 수업 날짜 (yyyy-MM-dd)
  private String status; // 출결 상태 (present, absent, late, left_early 등)

}
