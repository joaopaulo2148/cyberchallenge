package com.cyberchallenge.exception;

/** Lancada quando um dado que deveria ser unico ja existe (ex: nickname em uso). Mapeada para HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
