package br.com.fiap.valky.service;

import br.com.fiap.valky.dto.LoginDTO;
import br.com.fiap.valky.dto.TokenRespostaDTO;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.exception.CredenciaisInvalidasException;
import br.com.fiap.valky.repository.UsuarioRepository;
import br.com.fiap.valky.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Mesma mensagem para e-mail inexistente e senha errada (não revela quais e-mails existem). */
    public TokenRespostaDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return new TokenRespostaDTO(
                jwtService.gerarToken(usuario),
                "Bearer",
                jwtService.getExpiracaoSegundos(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getPerfil().name());
    }
}
