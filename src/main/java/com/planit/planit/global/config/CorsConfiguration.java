package com.planit.planit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000") // 필요 도메인들 명시
			.allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
			.allowedHeaders("Authorization","Content-Type")
			.allowCredentials(true)
			.maxAge(3600);
	}
}
