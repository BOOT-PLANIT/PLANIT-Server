package com.planit.planit.domain.enrollment.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MyBootcampDto {
	private Long id;
	private Long userId;
	private Long bootcampId;
	private String name;
	private String organizer;
	private Boolean isKdt;
	private LocalDate startedAt;
	private LocalDate endedAt;
	private Boolean isEnded;
}
