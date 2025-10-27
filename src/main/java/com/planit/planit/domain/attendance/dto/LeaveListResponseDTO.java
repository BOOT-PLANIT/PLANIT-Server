package com.planit.planit.domain.attendance.dto;

import com.planit.planit.domain.attendance.enums.AttendanceStatus;
import lombok.Data;

@Data
public class LeaveListResponseDTO {
  private int unitNo;
  private String classDate;
  private AttendanceStatus status;
}
