package com.cyberchallenge.exception;

/** Nickname inexistente ou senha incorreta no login. Mapeada para HTTP 401. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
