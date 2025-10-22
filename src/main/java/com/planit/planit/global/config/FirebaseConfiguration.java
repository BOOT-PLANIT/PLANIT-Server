package com.planit.planit.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfiguration {

	private static final String FIREBASE_KEY_PATH = "firebase/boot-planit-firebase-adminsdk-fbsvc-d7d3dd1d02.json";

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			var keyPath = FIREBASE_KEY_PATH.replace("${fcm.key-path:", "").replace("}", "");
			log.info("[Firebase] initializing... key: {}", keyPath);
			var credentials = GoogleCredentials.fromStream(
				new ClassPathResource(keyPath).getInputStream()
			);
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(credentials)
				.setProjectId("boot-planit")
				.build();
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
