package com.planit.planit.domain.unitperiod.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UnitPeriodDTO {
  @Schema(description = "단위기간 ID", example = "1")
  private Long id;

  @Schema(description = "부트캠프 ID", example = "1")
  private Long bootcampId;

  @Schema(description = "단위기간 번호", example = "1")
  private Integer unitNo;

  @Schema(description = "시작일", example = "2025-01-15")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-02-15")
  private LocalDate endDate;

  @Schema(description = "생성일시")
  private OffsetDateTime createdAt;
}
