package com.planit.planit.domain.attendance.dto;


import com.planit.planit.domain.attendance.enums.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AttendanceRegistRequestDTO {
  @Schema(description = "사용자 ID", example = "5")
  @NotNull(message = "사용자 ID는 필수입니다")
  private Long userId;

  @Schema(description = "부트캠프 ID", example = "1")
  @NotNull(message = "부트캠프 ID는 필수입니다")
  private Long bootcampId;

  @Schema(description = "출결 상태", example = "present")
  @NotNull(message = "출결 상태는 필수입니다")
  private AttendanceStatus status;

  @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
      message = "날짜는 YYYY-MM-DD 형식이어야 합니다. 예: 2025-10-23")
  @Schema(description = "선택한 날짜", example = "2025-09-01")
  @NotBlank(message = "날짜는 필수입니다")
  private String date;

}
