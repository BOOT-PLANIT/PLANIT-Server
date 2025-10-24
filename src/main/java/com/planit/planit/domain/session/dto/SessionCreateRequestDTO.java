package com.planit.planit.domain.session.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "세션 등록 요청 DTO")
public class SessionCreateRequestDTO {
  @Schema(description = "부트캠프 ID", example = "1", required = true)
  @NotNull(message = "부트캠프 ID는 필수입니다")
  private Long bootcampId;

  @Schema(description = "세션 목록", example = "[{\"classDate\": \"2025-01-15\"}, {\"classDate\": \"2025-01-16\"}]", required = true)
  @NotEmpty(message = "세션 목록은 비어있을 수 없습니다")
  @Valid
  private List<SessionCreateItemDTO> sessions;
}

