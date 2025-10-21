package com.planit.planit.domain.bootcamp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BootcampDTO {
    @Schema(description = "부트캠프 ID", example = "1")
    private Long id;

    @Schema(description = "부트캠프 이름", example = "LG 유플러스 유레카")
    @NotBlank(message = "부트캠프 이름은 필수입니다")
    private String name;

    @Schema(description = "기관 이름", example = "LG 유플러스")
    @NotBlank(message = "기관 이름은 필수입니다")
    private String organizer;

    @Schema(description = "K-Digital Training 여부", example = "false")
    @NotNull(message = "K-Digital Training 여부는 필수입니다")
    private Boolean isKdt;

    @Schema(description = "교육일 목록 (첫 날짜가 기준일이 되어 단위기간 자동 생성)",
        example = "[\"2025-01-25\", \"2025-01-26\", \"2025-01-27\"]")
    @NotEmpty(message = "교육일 목록은 최소 1개 이상이어야 합니다")
    private List<LocalDate> classDates;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "수정일시")
    private OffsetDateTime updatedAt;
}