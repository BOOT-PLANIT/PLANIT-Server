package com.planit.planit.domain.bootcamp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

/**
 * 내부 처리용 DTO (Mapper와 Service에서 사용)
 */
@Data
public class BootcampDTO {
	private Long id;
	private String name;
	private String organizer;
	private Boolean isKdt;
	private LocalDate startedAt;
	private LocalDate endedAt;
	private List<LocalDate> classDates;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
}
