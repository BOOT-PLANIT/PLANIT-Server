package com.planit.planit.domain.user.controller;

import com.google.firebase.messaging.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class NotificationTestController {

	@PostMapping("/push")
	public ResponseEntity<String> sendPush(@RequestParam String token) {
		try {
			Message message = Message.builder()
				.setToken(token)
				.setNotification(Notification.builder()
					.setTitle("Planit FCM 테스트 성공!")
					.setBody("브라우저와 서버가 FCM으로 연결되었습니다.")
					.build())
				.build();

			String response = FirebaseMessaging.getInstance().send(message);
			return ResponseEntity.ok("전송 성공: " + response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("전송 실패: " + e.getMessage());
		}
	}
}

