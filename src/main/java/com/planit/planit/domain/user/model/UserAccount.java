package com.planit.planit.domain.user.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
	private Long id;
	private String uid;
	private String email;
	private String displayName;
	private String photoUrl;
	private UserLevel userLevel;
	private String provider;
	private boolean emailVerified;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
}
