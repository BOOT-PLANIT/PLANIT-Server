package com.planit.planit.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveFcmTokenRequestDTO {
	@NotBlank(message = "fcmToken은 필수입니다.")
	private String fcmToken;
}
