package com.planit.planit.domain.notification.service;

import com.planit.planit.domain.user.mapper.UserMapper;
import com.planit.planit.domain.user.model.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

	private final UserMapper userMapper;
	private final NotificationService notificationService;

	private static final int MIN_VALID_TOKEN_LENGTH = 10;

	/** 오전 9시 */
	@Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
	public void morningNotification() {
		sendDailyNotification("입실 알람 🌞", "입실 체크를 해주세요!");
	}

	/** 오후 5시 50분 */
	@Scheduled(cron = "0 50 17 * * *", zone = "Asia/Seoul")
	public void eveningNotification() {
		sendDailyNotification("퇴실 알람 🌙", "퇴실 체크를 해주세요!");
	}

	// 대상 조회 & NotificationService 호출
	private void sendDailyNotification(String title, String body) {
		List<UserAccount> users = userMapper.findUsersWithAlarmOn();

		List<String> tokens = users.stream()
			.map(UserAccount::getFcmToken)
			.filter(token -> token != null && token.length() > MIN_VALID_TOKEN_LENGTH)
			.collect(Collectors.toList());

		if (tokens.isEmpty()) {
			log.warn("전송 대상 없음 (alarmOn=false 또는 fcmToken 미등록)");
			return;
		}

		log.info("총 {}명에게 [{}] 알림 전송 시작", tokens.size(), title);
		notificationService.sendNotification(title, body, tokens);
	}
}
