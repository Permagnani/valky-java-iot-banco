package br.com.fiap.valky.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Todas as respostas de erro seguem o formato de {@link ErroResposta}. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ConflitoException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErroResposta> tratarConflito(Exception ex) {
        String mensagem = ex instanceof ConflitoException
                ? ex.getMessage()
                : "Os dados enviados conflitam com um registro já existente.";
        return resposta(HttpStatus.CONFLICT, mensagem);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratarCredenciais(CredenciaisInvalidasException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResposta> tratarAcessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN,
                "Você não tem permissão para acessar este recurso.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> tratarRegraDeNegocio(IllegalArgumentException ex) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(ErroResposta.comCampos(
                status.value(), status.getReasonPhrase(),
                "Há campos inválidos na requisição.", campos));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> tratarRequisicaoMalFormada(Exception ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição malformada ou com valor inválido.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> tratarRotaInexistente(NoResourceFoundException ex) {
        return resposta(HttpStatus.NOT_FOUND, "Recurso não encontrado.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroInterno(Exception ex) {
        // detalhes só no log; o cliente nunca vê stack trace
        log.error("Erro inesperado", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.");
    }

    private ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(ErroResposta.de(status.value(), status.getReasonPhrase(), mensagem));
    }
}
