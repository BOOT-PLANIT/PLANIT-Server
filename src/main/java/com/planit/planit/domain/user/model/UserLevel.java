package com.planit.planit.domain.user.model;

public enum UserLevel {
	USER, ADMIN;

	public static UserLevel fromClaim(String s) {
		if (s == null) return USER;
		try {
			return UserLevel.valueOf(s.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return USER;
		}
	}

	public String asRole() {
		return "ROLE_" + name();
	}
}
