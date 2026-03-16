package com.analisys.gimnasio.miembros_service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
     * Configuración de OpenAPI/Swagger para documentación de la API.
     * 
     * para acceder a la documentación:
     * - Swagger UI: http://localhost:8080/swagger-ui.html
     * - OpenAPI JSON: http://localhost:8080/api-docs
     */
@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Gimnasio Miembros Service API")
                .version("1.0.0")
                .description("""
                    API REST para la gestión de miembros del gimnasio.
                    
                    ## Funcionalidades
                    - CRUD completo de miembros
                    - Gestión de membresías y pagos
                    - Control de asistencia
                    
                    ## Autenticación
                    Esta API utiliza OAuth2/JWT con Keycloak.
                    Para obtener un token, autentícate en Keycloak con las credenciales proporcionadas.
                    
                    ## Roles
                    - **ADMIN**: Acceso completo (CRUD)
                    - **TRAINER**: Puede ver miembros y gestionar asistencia
                    - **MEMBER**: Solo puede ver su propia información
                    """)
                .contact(new Contact()
                    .name("Equipo Gimnasio")
                    .email("soporte@gimnasio.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor local de desarrollo")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtenido de Keycloak"))
                .addSecuritySchemes("oauth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .flows(new OAuthFlows()
                        .password(new OAuthFlow()
                            .tokenUrl(issuerUri + "/protocol/openid-connect/token")))));
    }
                            
    
}
