package com.cyberchallenge.service;

import com.cyberchallenge.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Regras de senha forte (item 5 do briefing), inspiradas em recomendacoes
 * modernas de servicos como o Google: comprimento minimo razoavel
 * combinado com variedade de caracteres, mais uma lista de bloqueio para
 * senhas obviamente fracas -- sem exigencias artificialmente excessivas
 * (nao exige troca periodica, nem comprimentos exagerados).
 *
 * As MESMAS regras sao verificadas no front-end (para o checklist visual
 * em tempo real) e aqui no backend, que e quem realmente decide se o
 * cadastro e aceito -- o front-end nunca e a unica camada de validacao.
 */
@Component
public class PasswordValidator {

    private static final int TAMANHO_MINIMO = 8;
    private static final Pattern TEM_MAIUSCULA = Pattern.compile("[A-Z]");
    private static final Pattern TEM_MINUSCULA = Pattern.compile("[a-z]");
    private static final Pattern TEM_NUMERO = Pattern.compile("[0-9]");
    private static final Pattern TEM_ESPECIAL = Pattern.compile("[^A-Za-z0-9]");

    // Lista curta e representativa de senhas obviamente fracas/comuns.
    // Nao pretende ser exaustiva -- e uma ultima barreira contra os casos
    // mais obvios, combinada com as regras de composicao acima.
    private static final Set<String> SENHAS_COMUNS = Set.of(
        "12345678", "123456789", "1234567890", "password", "password1",
        "senha123", "senha1234", "qwerty123", "12345678910", "abc12345",
        "letmein1", "welcome1", "admin123", "iloveyou1", "00000000",
        "11111111", "87654321", "cyberchallenge"
    );

    public void validar(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO) {
            throw new RegraDeNegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO + " caracteres.");
        }
        if (!TEM_MAIUSCULA.matcher(senha).find()) {
            throw new RegraDeNegocioException("A senha deve conter ao menos uma letra maiuscula.");
        }
        if (!TEM_MINUSCULA.matcher(senha).find()) {
            throw new RegraDeNegocioException("A senha deve conter ao menos uma letra minuscula.");
        }
        if (!TEM_NUMERO.matcher(senha).find()) {
            throw new RegraDeNegocioException("A senha deve conter ao menos um numero.");
        }
        if (!TEM_ESPECIAL.matcher(senha).find()) {
            throw new RegraDeNegocioException("A senha deve conter ao menos um caractere especial.");
        }
        if (SENHAS_COMUNS.contains(senha.toLowerCase())) {
            throw new RegraDeNegocioException("Essa senha e muito comum e facil de adivinhar. Escolha outra.");
        }
    }
}
