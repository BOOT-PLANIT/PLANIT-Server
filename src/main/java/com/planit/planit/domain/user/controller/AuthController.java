package com.planit.planit.domain.user.controller;

import org.springframework.security.core.Authentication;
import com.planit.planit.domain.user.mapper.UserMapper;
import com.planit.planit.domain.user.model.UserAccount;
import com.planit.planit.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserMapper userMapper;

	@GetMapping("/me")
	public ApiResponse<UserAccount> me(Authentication auth) {
		String uid = auth.getName();
		var user = userMapper.findByUid(uid);
		return ApiResponse.success("내 정보", user);
	}
}

