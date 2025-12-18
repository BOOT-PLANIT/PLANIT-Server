package com.planit.planit.domain.user.dto;

import com.planit.planit.domain.user.model.UserLevel;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeResponseDTO {
	private Long id;
	private String uid;
	private String email;
	private String displayName;
	private String photoUrl;
	private UserLevel userLevel; // DB Default: USER
	private String provider;
	private boolean emailVerified;
	private LocalDateTime createdAt;  // DB Default
	private LocalDateTime lastLoginAt;   // DB Default
	private Long recentBootcampId;
}
