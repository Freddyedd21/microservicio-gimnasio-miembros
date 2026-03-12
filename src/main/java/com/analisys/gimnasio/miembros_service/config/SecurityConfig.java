package com.analisys.gimnasio.miembros_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/****
 * Configuración de seguridad con OAuth2/Keycloak.
 * 
 * Roles definidos en Keycloak:
 * - ROLE_ADMIN: Acceso completo a todos los endpoints
 * - ROLE_TRAINER: Puede ver y usar equipos
 * - ROLE_MEMBER: Puede ver equipos disponibles
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para APIs REST stateless
            .csrf(csrf -> csrf.disable())
            
            // Configurar sesiones como stateless (JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorización de endpoints
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (Swagger, H2 Console, Actuator)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/h2-console/**",
                    "/actuator/**"
                ).permitAll()
                
                // Miembros endpoints - Autorización basada en roles
                .requestMatchers(HttpMethod.GET, "/api/members/**").hasAnyRole("ADMIN", "TRAINER", "MEMBER")
                .requestMatchers(HttpMethod.POST, "/api/members/**").hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers(HttpMethod.PUT, "/api/members/**").hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar JWT Authentication Converter personalizado
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            )

            // Permitir frames para H2 Console (solo en desarrollo)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));


        return http.build();
    }

}
