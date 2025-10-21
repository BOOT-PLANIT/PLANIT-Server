package com.planit.planit.domain.bootcamp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BootcampDTO {
  @Schema(description = "부트캠프 ID", example = "1")
  private Long id;

  @Schema(description = "부트캠프 이름", example = "네이버 부스트캠프 9기")
  private String name;

  @Schema(description = "K-Digital Training 여부", example = "false")
  private Boolean isKdt;

  @Schema(description = "교육일 목록 (첫 날짜가 기준일이 되어 단위기간 자동 생성)",
      example = "[\"2025-01-25\", \"2025-01-26\", \"2025-01-27\"]")
  private List<LocalDate> classDates;

  @Schema(description = "생성일시")
  private OffsetDateTime createdAt;

  @Schema(description = "수정일시")
  private OffsetDateTime updatedAt;
}
