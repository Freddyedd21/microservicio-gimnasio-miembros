package com.analisys.gimnasio.miembros_service.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

/**
 * Converter personalizado para extraer roles de Keycloak desde el JWT.
 * 
 * Keycloak almacena los roles en diferentes ubicaciones del token:
 * - realm_access.roles: Roles a nivel de realm
 * - resource_access.{client}.roles: Roles específicos del cliente
 * 
 * Este converter extrae ambos tipos de roles y los convierte a GrantedAuthority
 * con el prefijo ROLE_ requerido por Spring Security.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = 
        new JwtGrantedAuthoritiesConverter();

    private static final String RESOURCE_ID = "gym-members-service";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
            jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
            extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        // Usar preferred_username si está disponible, de lo contrario usar sub
        String claimName = jwt.getClaimAsString("preferred_username");
        if (claimName == null) {
            claimName = jwt.getSubject();
        }
        return claimName;
    }

    /**
     * Extrae los roles del token JWT de Keycloak.
     * Busca en realm_access y resource_access.
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Set<GrantedAuthority> roles = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            Object roleObj = realmAccess.get("roles");
            if (roleObj instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<String> realmRoles = (Collection<String>) roleObj;
                roles.addAll(realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet())); 

            }
            
        }
        // Extraer roles específicos del cliente
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            Object clientRolesObj = resourceAccess.get(RESOURCE_ID);
            if (clientRolesObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> clientAccess = (Map<String, Object>) clientRolesObj;
                Object rolesObj = clientAccess.get("roles");
                if (rolesObj instanceof Collection) {
                    @SuppressWarnings("unchecked")
                    Collection<String> clientRoles = (Collection<String>) rolesObj;
                    roles.addAll(clientRoles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toSet()));
                }
            }
        }

        return roles;
    }

}