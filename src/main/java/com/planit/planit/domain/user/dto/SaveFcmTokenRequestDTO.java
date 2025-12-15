package com.planit.planit.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SaveFcmTokenRequestDTO {
	@NotBlank(message = "fcmToken은 필수입니다.")
	private String fcmToken;
}
