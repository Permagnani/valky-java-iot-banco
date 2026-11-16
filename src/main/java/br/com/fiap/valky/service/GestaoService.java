package br.com.fiap.valky.service;

import br.com.fiap.valky.dto.EstatisticasDTO;
import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.repository.ContatoConfiancaRepository;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/** Visão agregada para o GERENTE: só números, nunca dados pessoais ou da ficha. */
@Service
public class GestaoService {

    private final UsuarioRepository usuarioRepository;
    private final ContatoConfiancaRepository contatoRepository;
    private final FichaContextoRepository fichaRepository;

    public GestaoService(UsuarioRepository usuarioRepository,
                         ContatoConfiancaRepository contatoRepository,
                         FichaContextoRepository fichaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contatoRepository = contatoRepository;
        this.fichaRepository = fichaRepository;
    }

    public EstatisticasDTO estatisticas() {
        return new EstatisticasDTO(
                usuarioRepository.count(),
                usuarioRepository.countByPerfil(Perfil.CIDADA),
                usuarioRepository.countByPerfil(Perfil.GERENTE),
                contatoRepository.count(),
                fichaRepository.count(),
                fichaRepository.countByResumoStatus(ResumoStatus.GERADO));
    }
}
