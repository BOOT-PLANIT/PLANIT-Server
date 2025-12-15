package com.planit.planit.domain.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {
	private Long id;
	private Long userId;
	private Long bootcampId;
	private LocalDateTime enrolledAt;
}
