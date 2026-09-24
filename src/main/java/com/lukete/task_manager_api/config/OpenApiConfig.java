package com.lukete.task_manager_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI taskManagerOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Task Manager API")
						.version("v1")
						.description("REST API for task management."));
	}
}
