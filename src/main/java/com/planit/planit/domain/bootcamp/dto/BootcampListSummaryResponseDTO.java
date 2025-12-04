package com.planit.planit.domain.bootcamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "부트캠프 목록 요약 응답 DTO")
public class BootcampListSummaryResponseDTO {
	@Schema(description = "전체 부트캠프 개수", example = "50")
	private Long totalCount;

	@Schema(description = "진행중인 부트캠프 개수", example = "30")
	private Long activeCount;
}

