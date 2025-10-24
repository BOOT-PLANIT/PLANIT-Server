package com.planit.planit.domain.session.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "세션 삭제 요청 DTO")
public class SessionDeleteRequestDTO {
  @Schema(description = "삭제할 세션 ID 목록", example = "[1, 2, 3]", required = true)
  @NotEmpty(message = "삭제할 세션 ID 목록은 비어있을 수 없습니다")
  private List<Long> sessionIds;
}
