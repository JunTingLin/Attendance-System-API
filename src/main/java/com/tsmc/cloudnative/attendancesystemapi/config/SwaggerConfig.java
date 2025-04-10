package com.tsmc.cloudnative.attendancesystemapi.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Attendance System API")
                        .version("1.0")
                        .description("TSMC Cloud Native Final Project")
                        .contact(new Contact()
                                .name("Group 8")));
    }
}
