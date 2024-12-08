package com.task.socialnetwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @io.swagger.v3.oas.annotations.info.Info(title = "Mini Social Network API", version = "1.0"),
    security = @SecurityRequirement(name = "bearerAuth") // Reference to the security scheme
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Custom API Documentation")
            .version("1.0.0")
            .description("This is the API documentation for the Mini Social Network application.")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }

  @Bean
  public GroupedOpenApi allGroups() {
    return GroupedOpenApi.builder()
        .group("Mini Social Network API") // Name of the group in Swagger UI
        .pathsToMatch("/api/posts/**", "/api/auth/**",
            "/api/statistics/**", "/api/user/**") // Include only these paths
        .build();
  }
}