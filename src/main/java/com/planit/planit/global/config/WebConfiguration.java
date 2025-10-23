package com.planit.planit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry r) {
		r.addMapping("/**")
			.allowedOrigins("http://localhost:3000") // TODO 배포 도메인으로 교체
			.allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true)
			.maxAge(3600);
	}
}
