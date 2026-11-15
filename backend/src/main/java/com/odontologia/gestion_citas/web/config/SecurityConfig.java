// src/main/java/com/odontologia/gestion_citas/web/config/SecurityConfig.java
package com.odontologia.gestion_citas.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ODO-39: habilita @PreAuthorize (hoy solo lo usa /api/pagos)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Archivos públicos y documentación
                .requestMatchers(HttpMethod.GET, "/api/archivos/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // 2. Endpoints de lectura pública para catálogo y agendamiento inicial
                .requestMatchers(HttpMethod.GET, "/api/tratamientos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/lista/doctores").permitAll()

                // 3. Todas las demás rutas de la API requieren autenticación válida
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            // 4. Resource Server validando JWT de Keycloak
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * ODO-39: convierte los roles del realm de Keycloak (claim realm_access.roles) en autoridades ROLE_*.
     * No cambia las reglas existentes: las rutas actuales siguen exigiendo solo autenticación.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::rolesDelRealm);
        return converter;
    }

    @SuppressWarnings("unchecked")
    private static Collection<GrantedAuthority> rolesDelRealm(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
            return List.of();
        }
        return ((Collection<Object>) roles).stream()
                .map(String::valueOf)
                .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol))
                .toList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "https://*.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}