package br.com.fiap.valky.exception;

/** Recurso inexistente (ou que não pertence a quem pediu) - vira HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
