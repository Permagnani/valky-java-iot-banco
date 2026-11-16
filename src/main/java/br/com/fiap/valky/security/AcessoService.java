package br.com.fiap.valky.security;

import br.com.fiap.valky.entity.Perfil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Regras de "quem pode mexer em quê" além do papel:
 * a cidadã só acessa os próprios dados. Dados sensíveis (ficha e contatos)
 * não são acessíveis nem para o GERENTE.
 */
@Component
public class AcessoService {

    public Long usuarioId(Jwt jwt) {
        Object uid = jwt.getClaim(JwtService.CLAIM_USUARIO_ID);
        if (uid instanceof Number numero) {
            return numero.longValue();
        }
        throw new AccessDeniedException("Token sem identificação de usuário.");
    }

    public boolean ehGerente(Jwt jwt) {
        List<String> papeis = jwt.getClaimAsStringList(JwtService.CLAIM_PAPEIS);
        return papeis != null && papeis.contains(Perfil.GERENTE.name());
    }

    /** Somente a própria usuária. */
    public void exigirDona(Jwt jwt, Long usuarioIdAlvo) {
        if (!usuarioId(jwt).equals(usuarioIdAlvo)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    /** A própria usuária ou um gerente. */
    public void exigirDonaOuGerente(Jwt jwt, Long usuarioIdAlvo) {
        if (!ehGerente(jwt)) {
            exigirDona(jwt, usuarioIdAlvo);
        }
    }
}
