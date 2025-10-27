package com.planit.planit.global.config;

import com.planit.planit.global.security.CustomHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;


@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	// CORS 설정
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(
				"http://localhost:3000",
				"https://planit-web-eosin.vercel.app"
			) // 배포 시 도메인으로 교체
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600);
	}

	// 커스텀 HTTP Header 필터
	@Bean
	public Filter customHeaderFilter() {
		return new CustomHeaderFilter();
	}

	// 요청 로깅
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(false);
		return loggingFilter;
	}
}
