package br.com.fiap.valky.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger UI em /swagger-ui.html. Use o botão "Authorize" com o token do login. */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Valky API (api-java)",
        version = "1.0",
        description = "Contas, ficha de contexto, contatos de confiança e resumo por IA."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
