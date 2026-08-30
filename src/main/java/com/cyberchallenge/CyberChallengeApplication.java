package com.cyberchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe de bootstrap da aplicacao CYBER CHALLENGE.
 *
 * BUG CORRIGIDO: essa classe nao existia no projeto original. Sem uma classe
 * anotada com @SpringBootApplication, o Spring Boot nao tem ponto de entrada
 * e a aplicacao simplesmente nao sobe.
 */
@SpringBootApplication
public class CyberChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberChallengeApplication.class, args);
    }
}
