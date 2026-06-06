package com.empresa.sistemaventas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a absolutamente todos los endpoints
                .allowedOriginPatterns("*") // El secreto está aquí: Patrones abiertos en lugar de orígenes fijos
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false); // Como usaremos JWT en la cabecera (Header) y no Cookies, esto va en false
    }
}