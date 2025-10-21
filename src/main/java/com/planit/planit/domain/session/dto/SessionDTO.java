package com.planit.planit.domain.session.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionDTO {
    @Schema(description = "세션 ID", example = "1")
    private Long id;

    @Schema(description = "부트캠프 ID", example = "1")
    @NotNull(message = "부트캠프 ID는 필수입니다")
    private Long bootcampId;

    @Schema(description = "단위기간 ID", example = "1")
    @NotNull(message = "단위기간 ID는 필수입니다")
    private Long periodId;

    @Schema(description = "수업 날짜", example = "2025-01-15")
    @NotNull(message = "수업 날짜는 필수입니다")
    private LocalDate classDate;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "수정일시")
    private OffsetDateTime updatedAt;
}