package com.cyberchallenge.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serve explicitamente src/main/resources/static/admin/index.html em
 * GET /admin/.
 *
 * Motivo: diferente da raiz do site ("/"), o Spring Boot NAO resolve
 * automaticamente "index.html" para requisicoes de subpastas dos recursos
 * estaticos (isso so acontece para "/" via WelcomePageHandlerMapping). Sem
 * este controller, acessar "/admin/" resultaria em 404 mesmo com o arquivo
 * fisicamente presente em static/admin/index.html.
 *
 * O redirecionamento de "/admin" (sem barra) para "/admin/" fica no WebConfig.
 */
@RestController
public class AdminPageController {

    @GetMapping(value = "/admin/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> paginaAdmin() {
        return ResponseEntity.ok(new ClassPathResource("static/admin/index.html"));
    }
}
