package com.stockcontrol.RawMaterial.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI informationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Raw Material API")
                        .version("v1")
                        .description("Raw Material API - Service"));
    }
}
