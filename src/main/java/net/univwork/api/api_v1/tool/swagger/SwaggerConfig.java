package net.univwork.api.api_v1.tool.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInformation());
    }

    private Info apiInformation() {
        return new Info()
                .title("univwork.net API")
                .description("UNIVWORK REST API V1.0.0")
                .version("1.0.0");
    }
}
