package com.planit.planit.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfiguration {

	private static final String FIREBASE_CONFIG_PATH = "firebase/boot-planit-firebase-adminsdk-fbsvc-d7d3dd1d02.json";

	@PostConstruct
	public void initialize() {
		try {
			GoogleCredentials cred = GoogleCredentials.fromStream(
				new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()
			);
			FirebaseOptions options = FirebaseOptions.builder().setCredentials(cred).build();
			if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			log.error("FCM init error: {}", e.getMessage());
		}
	}
}

