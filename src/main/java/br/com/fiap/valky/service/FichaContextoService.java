package br.com.fiap.valky.service;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.messaging.FichaEventoPublisher;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FichaContextoService {

    private final FichaContextoRepository fichaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FichaEventoPublisher eventoPublisher;

    public FichaContextoService(
            FichaContextoRepository fichaRepository,
            UsuarioRepository usuarioRepository,
            FichaEventoPublisher eventoPublisher) {
        this.fichaRepository = fichaRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoPublisher = eventoPublisher;
    }

    /** @return a ficha da usuária ou null se ela ainda não preencheu. */
    public FichaContexto buscarPorUsuario(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);

        return fichaRepository.findByUsuario(usuario)
                .orElse(null);
    }

    /** Cria ou atualiza a ficha e dispara o evento que gera o resumo por IA. */
    public FichaContexto salvar(Long usuarioId, FichaContexto ficha) {
        Usuario usuario = buscarUsuario(usuarioId);

        FichaContexto existente = fichaRepository.findByUsuario(usuario)
                .orElse(new FichaContexto());

        existente.setUsuario(usuario);
        existente.setDataAtualizacao(LocalDateTime.now());
        existente.setInformacoesImportantes(ficha.getInformacoesImportantes());
        existente.setOrientacoesEmergencia(ficha.getOrientacoesEmergencia());
        existente.setNomeAgressor(ficha.getNomeAgressor());
        existente.setRelacaoAgressor(ficha.getRelacaoAgressor());
        existente.setPossuiArma(ficha.getPossuiArma());
        existente.setVeiculo(ficha.getVeiculo());
        existente.setEnderecosConhecidos(ficha.getEnderecosConhecidos());
        existente.setNumeroBo(ficha.getNumeroBo());
        existente.setMedidaProtetiva(ficha.getMedidaProtetiva());

        // a ficha mudou: o resumo antigo não vale mais
        existente.setResumoIa(null);
        existente.setResumoGeradoEm(null);
        existente.setResumoStatus(ResumoStatus.PENDENTE);

        FichaContexto salva = fichaRepository.save(existente);

        eventoPublisher.publicarFichaAtualizada(salva.getId());

        return salva;
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
