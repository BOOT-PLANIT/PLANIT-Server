package com.planit.planit.domain.enrollment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class MyBootcampDTO {
	private Long id;
	private Long userId;
	private Long bootcampId;
	private String name;
	private String organizer;
	private Boolean isKdt;
	private LocalDate startedAt;      // 부트캠프 시작일
	private LocalDate endedAt;        // 부트캠프 종료일
	private LocalDateTime enrolledAt; // 내 부트캠프로 등록한 시각
	private Boolean isEnded;          // 부트캠프 종료 여부
}
