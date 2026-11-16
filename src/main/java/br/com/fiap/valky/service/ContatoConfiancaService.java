package br.com.fiap.valky.service;

import br.com.fiap.valky.entity.ContatoConfianca;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
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
        return contatoRepository.findByUsuario(buscarUsuario(usuarioId));
    }

    public ContatoConfianca cadastrar(Long usuarioId, ContatoConfianca contato) {
        contato.setUsuario(buscarUsuario(usuarioId));

        return contatoRepository.save(contato);
    }

    /** Só exclui se o contato pertencer à usuária; senão responde como "não encontrado". */
    public void excluir(Long id, Long usuarioId) {
        ContatoConfianca contato = contatoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Contato não encontrado."));

        contatoRepository.delete(contato);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
