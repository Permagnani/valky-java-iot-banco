package br.com.fiap.valky.config;

import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Cria um GERENTE e uma CIDADA de demonstração (o cadastro público só cria CIDADA,
 * então sem isto não haveria como testar o perfil GERENTE).
 * Desligue com SEED_ENABLED=false fora de ambiente de demonstração.
 */
@Component
@Profile("!test")
@ConditionalOnProperty(name = "valky.seed.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioService usuarioService;
    private final String gerenteEmail;
    private final String gerenteSenha;
    private final String cidadaEmail;
    private final String cidadaSenha;

    public DataInitializer(
            UsuarioService usuarioService,
            @Value("${valky.seed.gerente-email}") String gerenteEmail,
            @Value("${valky.seed.gerente-senha}") String gerenteSenha,
            @Value("${valky.seed.cidada-email}") String cidadaEmail,
            @Value("${valky.seed.cidada-senha}") String cidadaSenha) {
        this.usuarioService = usuarioService;
        this.gerenteEmail = gerenteEmail;
        this.gerenteSenha = gerenteSenha;
        this.cidadaEmail = cidadaEmail;
        this.cidadaSenha = cidadaSenha;
    }

    @Override
    public void run(String... args) {
        criarSeNaoExistir("Gerente Valky", gerenteEmail, gerenteSenha, Perfil.GERENTE);
        criarSeNaoExistir("Cidadã Valky", cidadaEmail, cidadaSenha, Perfil.CIDADA);
    }

    private void criarSeNaoExistir(String nome, String email, String senha, Perfil perfil) {
        if (usuarioService.emailJaCadastrado(email)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setPerfil(perfil);

        usuarioService.cadastrar(usuario);
        log.info("Usuário de demonstração criado: {} ({})", email, perfil);
    }
}
