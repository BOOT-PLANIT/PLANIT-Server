package com.planit.planit.domain.attendance.dto;

import java.time.LocalDate;
import com.planit.planit.domain.attendance.enums.AttendanceStatus;
import lombok.Data;

@Data
public class AttendanceDTO {

  private Long id; // 출결 ID
  private Long userId; // 사용자 ID
  private Long sessionId; // 세션 ID
  private Long periodId; // 교시 ID
  private AttendanceStatus status; // 출결 상태
  private LocalDate createdAt; // 생성 시각
  private LocalDate updatedAt; // 수정 시각
}
