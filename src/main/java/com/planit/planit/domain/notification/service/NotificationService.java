package com.planit.planit.domain.notification.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationService {

	private static final int BATCH_SIZE = 500; // FCM 제한
	private static final int MAX_RETRY = 3;    // 최대 재시도 횟수
	private static final int RETRY_DELAY_MS = 2000; // 재시도 간 대기 시간 2초

	// 500개 단위로 자동 분할 + 비동기 전송
	@Async
	public void sendNotification(String title, String body, List<String> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			log.warn("⚠️ No tokens provided for notification");
			return;
		}

		// 500개씩 분할
		List<List<String>> batches = splitList(tokens);

		for (int i = 0; i < batches.size(); i++) {
			List<String> batch = batches.get(i);
			int batchNum = i + 1;
			sendBatchWithRetry(title, body, batch, batchNum, 0);
		}
	}

	private void sendBatchWithRetry(String title, String body, List<String> tokens, int batchNum, int retryCount) {
		try {
			MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.addAllTokens(tokens)
				.build();

			BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

			int success = response.getSuccessCount();
			int failure = response.getFailureCount();

			log.info("[{}번째 배치] 성공: {}, 실패: {}, 재시도 횟수: {}",
				batchNum, success, failure, retryCount);

			// 실패한 토큰 수집
			List<String> failedTokens = new ArrayList<>();
			for (int i = 0; i < response.getResponses().size(); i++) {
				if (!response.getResponses().get(i).isSuccessful()) {
					String error = response.getResponses().get(i).getException().getMessage();
					String token = tokens.get(i);
					failedTokens.add(token);
					log.warn("[{}번째 배치] 실패 토큰: {}, 이유: {}", batchNum, token, error);
				}
			}

			// 실패한 토큰이 있고, 재시도 가능하면 다시 시도
			if (!failedTokens.isEmpty() && retryCount < MAX_RETRY) {
				log.warn("[{}번째 배치] 실패한 {}개 토큰 재전송 시도 ({}/{})",
					batchNum, failedTokens.size(), retryCount + 1, MAX_RETRY);
				TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
				sendBatchWithRetry(title, body, failedTokens, batchNum, retryCount + 1);
			} else if (!failedTokens.isEmpty()) {
				log.error("[{}번째 배치] 최대 재시도 횟수 초과 ({}개 토큰 전송 실패)",
					batchNum, failedTokens.size());
			}

		} catch (Exception e) {
			log.error("[{}번째 배치] FCM 전송 중 예외 발생 (재시도 {}회)", batchNum, retryCount, e);
		}
	}

	private <T> List<List<T>> splitList(List<T> list) {
		List<List<T>> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i += NotificationService.BATCH_SIZE) {
			result.add(list.subList(i, Math.min(list.size(), i + NotificationService.BATCH_SIZE)));
		}
		return result;
	}
}
