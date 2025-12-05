package com.planit.planit.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class LoginResponseDTO {
	private Long userId;
	private Long recentBootcampId; // 없으면 null
	private UserDetails userDetails;
}
