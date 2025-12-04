package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class AttendancePeriodResponseDTO {
  private Long userId;
  private Integer unitNo;

  // 단위기간 시작과 끝 날짜
  private String startDate;
  private String endDate;

  private Integer presentCount;
  private Integer absentCount;
  private Integer lateCount;
  private Integer leftEarlyCount;
  private Integer annualCount;
  private Integer leaveCount;

  private Integer totalPresentCount; // 실제 총 출석수 (출석,지각,조퇴,연차,휴가의합 에서 (지각+조퇴)/3 뺀값)
  private Integer totalAbsentCount;// 실제 총 결석수(결석+(지각+조퇴)/3)

  private Integer totalSubsidy; // 단위기간마다의 훈련지원금
  private Integer totalSessions;// 단위기간마다의 강의수



}
