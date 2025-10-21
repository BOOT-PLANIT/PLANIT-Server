package com.planit.planit.domain.attendance.dto;

import java.time.LocalDate;

import com.planit.planit.domain.attendance.enums.AttendanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AttendanceRegistRequestDTO {
	@Schema(description = "사용자 ID", example = "5")
	private long userId;
	
	@Schema(description = "부트캠프 ID", example = "1")
	private long bootcampId;
	
	@Schema(description = "출결 상태", example = "PRESENT")
	private AttendanceStatus status;
	
	@Schema(description = "선택한 날짜", example = "2025-09-01")
	private String date;

}
