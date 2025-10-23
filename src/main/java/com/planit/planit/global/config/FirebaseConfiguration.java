package com.planit.planit.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfiguration {

	@Value("${fcm.key-path:classpath:firebase/boot-planit-firebase-adminsdk-fbsvc-d7d3dd1d02.json}")
	private Resource firebaseSdkKey;

	@Value("${fcm.project-id:}")
	private String firebaseProjectId;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			log.info("[Firebase] initializing...");
			GoogleCredentials credentials;
			try (InputStream in = firebaseSdkKey.getInputStream()) {
				credentials = GoogleCredentials.fromStream(in);
			}
			FirebaseOptions.Builder builder = FirebaseOptions.builder()
				.setCredentials(credentials);
			if (firebaseProjectId != null && !firebaseProjectId.isBlank()) {
				builder.setProjectId(firebaseProjectId);
			}
			FirebaseOptions options = builder.build();
			FirebaseApp app = FirebaseApp.initializeApp(options);
			log.info("[Firebase] initialized: {}", app.getName());
			return app;
		}
		return FirebaseApp.getInstance();
	}

	@Bean
	public FirebaseAuth firebaseAuth(FirebaseApp app) {
		return FirebaseAuth.getInstance(app);
	}

	@Bean
	public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
		return FirebaseMessaging.getInstance(app);
	}
}
