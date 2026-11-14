
package br.com.fiap.valky.service;

import br.com.fiap.valky.entity.ContatoConfianca;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.repository.ContatoConfiancaRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContatoConfiancaService {

    private final ContatoConfiancaRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;

    public ContatoConfiancaService(
            ContatoConfiancaRepository contatoRepository,
            UsuarioRepository usuarioRepository) {
        this.contatoRepository = contatoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ContatoConfianca> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado."));

        return contatoRepository.findByUsuario(usuario);
    }

    public ContatoConfianca cadastrar(Long usuarioId, ContatoConfianca contato) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado."));

        contato.setUsuario(usuario);

        return contatoRepository.save(contato);
    }

    public void excluir(Long id) {
        ContatoConfianca contato = contatoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contato não encontrado."));

        contatoRepository.delete(contato);
    }
}
