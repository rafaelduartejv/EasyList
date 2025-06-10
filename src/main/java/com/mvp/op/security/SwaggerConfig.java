package com.mvp.op.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyList API")
                        .version("0.0.1")
                        .description("""
                            EasyList API provides endpoints for managing Mercado Livre listings, including:
                            - Secure user authentication with JWT
                            - OAuth2 integration with Mercado Livre
                            - Cloning, editing, saving drafts, and publishing product listings
                            - Token management and user info retrieval
                            
                            This API enables automation of product listings and order management workflows.
                            """));
    }
}
