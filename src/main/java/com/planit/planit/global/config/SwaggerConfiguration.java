package com.planit.planit.global.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

// Swagger-UI 확인
// http://localhost:8080/swagger-ui/index.html

@Configuration
public class SwaggerConfiguration {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public OpenAPI openUrecaAPI() {
		logger.debug("openUrecaAPI.............");

		final String BEARER = "BearerAuth";

		return new OpenAPI()
			.info(new Info()
				.title("planit SpringTest API 명세서")
				.description("<h3>SpringTest API Reference for Developers</h3>Swagger를 이용한 SpringTest API<br>")
				.version("v1"))
			.components(new Components()
				.addSecuritySchemes(BEARER,
					new SecurityScheme()
						.name(BEARER)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
			// 모든 엔드포인트에 Bearer 적용
			.addSecurityItem(new SecurityRequirement().addList(BEARER));
	}
}

