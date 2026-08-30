package com.cyberchallenge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS centralizado aqui em vez de espalhado com @CrossOrigin em cada
 * controller (como estava no PartidaController original). Assim, quando for
 * hora de restringir a origem para o dominio real do front-end, muda-se em
 * um unico lugar.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // TODO: restringir para a URL do front-end em producao
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }

    /**
     * BUG POTENCIAL EVITADO: ao contrario da raiz ("/"), o Spring Boot NAO
     * resolve automaticamente "index.html" para URLs de subpastas dos
     * recursos estaticos. Sem isso, "/admin" e ate "/admin/" dariam 404
     * mesmo com o arquivo em static/admin/index.html. Por isso o
     * redirecionamento aqui e o AdminPageController tratando "/admin/"
     * explicitamente.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/admin", "/admin/");
    }
}

