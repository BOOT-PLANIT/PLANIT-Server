package com.planit.planit.domain.attendance.dto;

import lombok.Data;

@Data
public class AttendanceTotalResponseDTO {
  private Long userId;
  private Integer unitNo;
  private Integer presentCount;
  private Integer absentCount;
  private Integer lateCount;
  private Integer leftEarlyCount;
  private Integer annualCount;
  private Integer leaveCount;

  private Integer totalPresentCount; // 실제 총 출석수 (출석,지각,조퇴,연차,휴가의합 에서 (지각+조퇴)/3 뺀값)
  private Integer totalAbsentCount;// 실제 총 결석수(결석+(지각+조퇴)/3)

  private Integer totalSubsidy; // 전체&단위기간마다의 훈련지원금
  private Integer totalSessions;// 전체&단위기간마다의 강의수



}
