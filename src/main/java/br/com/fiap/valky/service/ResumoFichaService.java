package br.com.fiap.valky.service;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.ia.GeradorResumo;
import br.com.fiap.valky.repository.FichaContextoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Orquestra a geração do resumo. Não é @Transactional de propósito: a chamada ao
 * LLM pode demorar e não deve segurar conexão do banco.
 */
@Service
public class ResumoFichaService {

    private static final Logger log = LoggerFactory.getLogger(ResumoFichaService.class);

    /** Fixo, para que o rótulo exigido pela regra do projeto nunca dependa do modelo. */
    public static final String AVISO =
            "Possível envolvido, segundo informação da usuária, não verificada.";

    private static final int LIMITE_RESUMO = 1900;

    private final FichaContextoRepository fichaRepository;
    private final GeradorResumo gerador;

    public ResumoFichaService(FichaContextoRepository fichaRepository,
                              GeradorResumo gerador) {
        this.fichaRepository = fichaRepository;
        this.gerador = gerador;
    }

    /** Gera (ou tenta gerar) o resumo e devolve a ficha como ficou no banco. */
    public FichaContexto gerarParaFicha(Long fichaId) {
        FichaContexto ficha = fichaRepository.findById(fichaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Ficha de contexto não encontrada."));

        LocalDateTime versao = ficha.getDataAtualizacao();
        String resumo = null;
        ResumoStatus status;

        try {
            String texto = gerador.gerar(ficha);
            if (texto == null || texto.isBlank()) {
                throw new IllegalStateException("A IA devolveu uma resposta vazia.");
            }
            resumo = montarResumo(texto);
            status = ResumoStatus.GERADO;
        } catch (Exception e) {
            log.warn("Falha ao gerar resumo da ficha {}: {}", fichaId, e.getMessage());
            status = ResumoStatus.FALHA;
        }

        int gravou = fichaRepository.atualizarResumo(
                fichaId, versao, resumo, status, LocalDateTime.now());

        if (gravou == 0) {
            log.info("Ficha {} foi editada durante a geração; resumo descartado "
                    + "(um novo evento já cuida da versão nova).", fichaId);
        }

        return fichaRepository.findById(fichaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Ficha de contexto não encontrada."));
    }

    static String montarResumo(String textoDaIa) {
        String completo = AVISO + "\n\n" + textoDaIa.trim();
        return completo.length() > LIMITE_RESUMO
                ? completo.substring(0, LIMITE_RESUMO)
                : completo;
    }
}
