package com.jairath.websocket.poc.impl.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("jairath.temporal.poc")
                                .version("v1.0"));
        }

        @Bean
        public OpenApiCustomiser customiseOpenAPI() {
                return openApi -> {
                        openApi.getPaths().values().forEach(pathItem -> {
                        });
                };
        }


}