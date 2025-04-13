package com.tsmc.cloudnative.attendancesystemapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";  // Define the security scheme name
        return new OpenAPI()
                .info(new Info()
                        .title("Attendance System API")
                        .version("1.0")
                        .description("TSMC Cloud Native Final Project")
                        .contact(new Contact().name("Group 8")))
                // Add security requirements globally for all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Define the BearerAuth scheme for JWT
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        // 增加檔案上傳用的schema
                        .addSchemas("file", new io.swagger.v3.oas.models.media.Schema<>()
                                .type("string")
                                .format("binary"))
                );
    }
}
