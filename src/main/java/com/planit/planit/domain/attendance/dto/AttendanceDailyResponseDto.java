package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class AttendanceDailyResponseDto {

  private Long attendanceId; // 출결 ID
  private String status; // 출결 상태(present, absent, late, left_early 등)

  private Long bootcampId; // 부트캠프 ID
  private String bootcampName; // 부트캠프 이름

  private Long sessionId; // 세션 ID

  private String classDate; // 수업 날짜

  private Integer unitNo; // 단위번호
  private String startDate; // 단위기간 시작일
  private String endDate; // 단위기간 종료일

}
