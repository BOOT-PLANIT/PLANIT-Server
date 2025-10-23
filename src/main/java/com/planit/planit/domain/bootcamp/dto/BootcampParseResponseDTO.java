package com.planit.planit.domain.bootcamp.dto;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "부트캠프 파싱 응답 DTO")
public class BootcampParseResponseDTO {
  @Schema(description = "훈련과정명 (부트캠프 이름)", example = "LG 유플러스 유레카")
  private String name;

  @Schema(description = "훈련기관명 (기관 이름)", example = "LG 유플러스")
  private String organizer;

  @Schema(description = "교육일 목록", example = "[\"2025-01-25\", \"2025-01-26\", \"2025-01-27\"]")
  private List<LocalDate> classDates;
}

