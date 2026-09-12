package com.cyberchallenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS centralizado aqui em vez de espalhado com @CrossOrigin em cada
 * controller. A origem e configuravel por variavel de ambiente
 * (APP_BASE_URL), para restringir facilmente ao dominio real do front-end
 * em producao.
 *
 * Reformulacao: o jogo nao usa mais sessao/cookies de autenticacao (o
 * sistema de contas foi removido por completo), entao "allowCredentials"
 * nao e mais necessario -- CORS simples, sem essa exigencia extra.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(appBaseUrl, "http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
