package com.planit.planit.domain.enrollment.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MyBootcampDto {
	private Long enrollmentId;
	private Long bootcampId;
	private String name;
	private String organizer;
	private String status;
	private LocalDate startedAt;
	private LocalDate endedAt;
}
