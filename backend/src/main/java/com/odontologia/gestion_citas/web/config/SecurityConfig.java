// src/main/java/com/odontologia/gestion_citas/web/config/SecurityConfig.java
package com.odontologia.gestion_citas.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            
            
            .authorizeHttpRequests(auth -> auth
                // 1. Archivos públicos
                .requestMatchers(HttpMethod.GET, "/api/archivos/**").permitAll()

                // 2. Swagger (Documentación)
                .requestMatchers(HttpMethod.GET,
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html"
                ).permitAll()
                
                // 3. Todas las demás rutas API requieren autenticación
                .requestMatchers("/api/**").authenticated()
                
                // Cualquier otra petición no mapeada también debe estar autenticada por seguridad
                .anyRequest().authenticated()
            )
            
            // 4. Le decimos a Spring Boot que actúe como un "Resource Server" validando tokens JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }
}