package com.planit.planit.global.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

//Swagger-UI 확인
//http://localhost:8080/planit/swagger-ui/index.html

////////////TODO 3. swagger configuration
@Configuration
public class SwaggerConfiguration {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public SwaggerConfiguration() {
		logger.debug("SwaggerConfiguration.................");
	}
	@Bean
	public OpenAPI openUrecaAPI() {
		logger.debug("openUrecaAPI.............");
		Info info = new Info().title("planit SpringTest API 명세서").description(
				"<h3>SpringTest API Reference for Developers</h3>Swagger를 이용한 SpringTest API<br>")
				.version("v1");

		return new OpenAPI().components(new Components()).info(info);
	}

}
