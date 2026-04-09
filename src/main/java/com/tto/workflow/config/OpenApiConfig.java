package com.tto.workflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Camunda backend")
                .description(
                    "APIs propias: paridad con la REST 7.24 bajo /api/v1/camunda y extensiones bajo /api/v1/custom.")
                .version("1.0.0"))
        .servers(List.of(new Server().url("/").description("Servidor actual")));
  }

  @Bean
  public GroupedOpenApi camundaParityApi() {
    return GroupedOpenApi.builder()
        .group("camunda-parity")
        .displayName("API Camunda (paridad)")
        .pathsToMatch("/api/v1/camunda/**")
        .build();
  }

  @Bean
  public GroupedOpenApi customApi() {
    return GroupedOpenApi.builder()
        .group("custom")
        .displayName("APIs personalizadas")
        .pathsToMatch("/api/v1/custom/**")
        .build();
  }
}
