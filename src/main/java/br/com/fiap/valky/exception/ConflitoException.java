package br.com.fiap.valky.exception;

/** Violação de regra de negócio por conflito de dados (ex.: e-mail repetido) - HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
