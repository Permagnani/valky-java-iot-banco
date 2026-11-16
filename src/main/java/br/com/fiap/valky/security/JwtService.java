package br.com.fiap.valky.security;

import br.com.fiap.valky.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Gera o token JWT (HS256) devolvido no login. A validação é feita pelo JwtDecoder. */
@Service
public class JwtService {

    public static final String ISSUER = "valky-api";
    public static final String CLAIM_USUARIO_ID = "uid";
    public static final String CLAIM_PAPEIS = "roles";

    private final JwtEncoder jwtEncoder;
    private final long expiracaoSegundos;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${valky.jwt.expiracao-segundos:3600}") long expiracaoSegundos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracaoSegundos = expiracaoSegundos;
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracaoSegundos))
                .subject(usuario.getEmail())
                .claim(CLAIM_USUARIO_ID, usuario.getId())
                .claim(CLAIM_PAPEIS, List.of(usuario.getPerfil().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    public long getExpiracaoSegundos() {
        return expiracaoSegundos;
    }
}
