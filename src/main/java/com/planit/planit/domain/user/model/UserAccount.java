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
	private boolean emailVerified;
	private boolean isDeleted;
	private LocalDateTime deletedAt;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
	private String fcmToken; // 푸시 알림용 토큰
	private boolean alarmOn;
}
