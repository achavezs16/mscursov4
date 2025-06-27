package com.mscursosv3.mscursosv3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Cursos-API EduTech")
                .version("1.0")
                .description("Gestión de Cursos (Documentación API)"));
    }

    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return new DelegatingLinkRelationProvider();
    }

}
