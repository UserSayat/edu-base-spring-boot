package com.example.edu_base.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eduBaseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Edu Base API")
                        .description("REST API for the Edu Base application")
                        .version("v1")
                        .contact(new Contact().name("Edu Base Team").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
