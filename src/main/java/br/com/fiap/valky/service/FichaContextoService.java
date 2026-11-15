
package br.com.fiap.valky.service;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class FichaContextoService {

    private final FichaContextoRepository fichaRepository;
    private final UsuarioRepository usuarioRepository;

    public FichaContextoService(
            FichaContextoRepository fichaRepository,
            UsuarioRepository usuarioRepository) {
        this.fichaRepository = fichaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public FichaContexto buscarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado."));

        return fichaRepository.findByUsuario(usuario)
                .orElse(null);
    }


    public FichaContexto salvar(Long usuarioId, FichaContexto ficha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado."));

        FichaContexto fichaExistente = fichaRepository.findByUsuario(usuario)
                .orElse(new FichaContexto());

        fichaExistente.setUsuario(usuario);
        fichaExistente.setDataAtualizacao(java.time.LocalDateTime.now());
        fichaExistente.setInformacoesImportantes(
                ficha.getInformacoesImportantes());
        fichaExistente.setOrientacoesEmergencia(
                ficha.getOrientacoesEmergencia());

        return fichaRepository.save(fichaExistente);
    }

}
