//src/main/java/com/odontologia/gestion_citas/web/config/CorsConfig.java
package com.odontologia.gestion_citas.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; 
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") 
                        .allowedOriginPatterns(
                            "http://localhost:4200",
                            "https://*.vercel.app",
                            "https://odontostyle-frontend-angular-*.vercel.app"
                        ) 
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") 
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}