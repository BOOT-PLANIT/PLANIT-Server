package com.planit.planit.domain.bootcamp.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "부트캠프 목록 요약 응답 DTO")
public class BootcampListSummaryResponseDTO {
	@Schema(description = "전체 부트캠프 개수", example = "50")
	private Long totalCount;

	@Schema(description = "종료되지 않은 부트캠프 개수", example = "30")
	private Long activeCount;

	@Schema(description = "부트캠프 목록")
	private List<BootcampResponseDTO> bootcamps;
}

