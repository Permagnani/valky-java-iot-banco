package br.com.fiap.valky.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato único de erro devolvido pela API.
 * "campos" só vem preenchido em erros de validação (campo -> mensagem).
 */
public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos) {

    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, null);
    }

    public static ErroResposta comCampos(int status, String erro, String mensagem,
                                         Map<String, String> campos) {
        return new ErroResposta(LocalDateTime.now(), status, erro, mensagem, campos);
    }
}
