package br.com.fiap.valky.service;

import br.com.fiap.valky.dto.UsuarioCadastroDTO;
import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.exception.ConflitoException;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean emailJaCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /** Cadastro público: sempre cria uma CIDADA, ignorando qualquer tentativa de escolher perfil. */
    public Usuario cadastrar(UsuarioCadastroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTelefone(dto.telefone());
        usuario.setPerfil(Perfil.CIDADA);

        return cadastrar(usuario);
    }

    /** Uso interno (ex.: carga inicial): respeita o perfil do objeto recebido. */
    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario dados) {
        Usuario usuario = buscarPorId(id);

        if (!usuario.getEmail().equals(dados.getEmail())
                && usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado.");
        }

        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setTelefone(dados.getTelefone());

        return usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {
        usuarioRepository.delete(buscarPorId(id));
    }
}
