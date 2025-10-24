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
	private UserLevel userLevel; // DB Default: USER
	private String provider;
	private Boolean emailVerified;
	private Boolean isDeleted;
	private LocalDateTime deletedAt;
	private LocalDateTime createdAt;  // DB Default
	private LocalDateTime lastLoginAt;   // DB Default
}
