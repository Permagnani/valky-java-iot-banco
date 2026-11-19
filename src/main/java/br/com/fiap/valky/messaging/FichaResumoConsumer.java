package br.com.fiap.valky.messaging;

import br.com.fiap.valky.config.RabbitConfig;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.service.ResumoFichaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consome "ficha atualizada" e gera o resumo com Spring AI, fora da requisição HTTP. */
@Component
public class FichaResumoConsumer {

    private static final Logger log = LoggerFactory.getLogger(FichaResumoConsumer.class);

    private final ResumoFichaService resumoFichaService;

    public FichaResumoConsumer(ResumoFichaService resumoFichaService) {
        this.resumoFichaService = resumoFichaService;
    }

    @RabbitListener(queues = RabbitConfig.FILA_RESUMO)
    public void aoReceber(String fichaId) {
        log.info("Mensagem recebida em {} (fichaId={})", RabbitConfig.FILA_RESUMO, fichaId);
        try {
            resumoFichaService.gerarParaFicha(Long.valueOf(fichaId.trim()));
        } catch (RecursoNaoEncontradoException | NumberFormatException e) {
            // ficha apagada (ou mensagem inválida): descartar, não adianta tentar de novo
            log.warn("Mensagem descartada (fichaId={}): {}", fichaId, e.getMessage());
        }
    }
}
