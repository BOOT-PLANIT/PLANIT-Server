package com.planit.planit.domain.session.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "세션 등록 아이템 DTO")
public class SessionCreateItemDTO {
  @Schema(description = "수업 날짜", example = "2025-01-15", required = true)
  @NotNull(message = "수업 날짜는 필수입니다")
  private LocalDate classDate;
}
