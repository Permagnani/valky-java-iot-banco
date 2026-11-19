package br.com.fiap.valky;

import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.ia.GeradorResumo;
import br.com.fiap.valky.messaging.FichaEventoPublisher;
import br.com.fiap.valky.security.JwtService;
import br.com.fiap.valky.service.ResumoFichaService;
import br.com.fiap.valky.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Fluxo ponta a ponta de login, papéis (CIDADA/GERENTE), ficha e resumo por IA. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SegurancaIntegracaoTest {

    private static final String SENHA = "Senha@1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    // sem RabbitMQ nem LLM de verdade nos testes
    @MockitoBean
    private FichaEventoPublisher eventoPublisher;

    @MockitoBean
    private GeradorResumo geradorResumo;

    private Usuario criar(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste " + perfil);
        usuario.setEmail(UUID.randomUUID() + "@teste.com");
        usuario.setSenha(SENHA);
        usuario.setPerfil(perfil);
        return usuarioService.cadastrar(usuario);
    }

    private String bearer(Usuario usuario) {
        return "Bearer " + jwtService.gerarToken(usuario);
    }

    private static final String FICHA_JSON = """
            {
              "informacoesImportantes": "Ele conhece meu trabalho.",
              "orientacoesEmergencia": "Chegar sem sirene.",
              "nomeAgressor": "Fulano",
              "possuiArma": true,
              "numeroBo": "123/2026"
            }
            """;

    @Test
    void cadastroPublicoDeveCriarSempreCidadaMesmoSePedirGerente() throws Exception {
        String json = """
                {
                  "nome": "Maria",
                  "email": "%s@teste.com",
                  "senha": "Senha@1234",
                  "perfil": "GERENTE"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("CIDADA"));
    }

    @Test
    void loginValidoDeveDevolverTokenEPerfil() throws Exception {
        Usuario usuario = criar(Perfil.CIDADA);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"%s\"}"
                                .formatted(usuario.getEmail(), SENHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.perfil").value("CIDADA"))
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()));
    }

    @Test
    void loginComSenhaErradaDeveRetornar401() throws Exception {
        Usuario usuario = criar(Perfil.CIDADA);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"senha\":\"errada-123\"}"
                                .formatted(usuario.getEmail())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    @Test
    void cidadaNaoPodeListarUsuariosMasGerentePode() throws Exception {
        Usuario cidada = criar(Perfil.CIDADA);
        Usuario gerente = criar(Perfil.GERENTE);

        mockMvc.perform(get("/api/usuarios").header("Authorization", bearer(cidada)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/usuarios").header("Authorization", bearer(gerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void somenteGerentePodeVerEstatisticas() throws Exception {
        Usuario cidada = criar(Perfil.CIDADA);
        Usuario gerente = criar(Perfil.GERENTE);

        mockMvc.perform(get("/api/gestao/estatisticas").header("Authorization", bearer(cidada)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/gestao/estatisticas").header("Authorization", bearer(gerente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").isNumber());
    }

    @Test
    void cidadaNaoPodeAcessarDadosDeOutraCidada() throws Exception {
        Usuario dona = criar(Perfil.CIDADA);
        Usuario outra = criar(Perfil.CIDADA);

        mockMvc.perform(get("/api/usuarios/" + dona.getId())
                        .header("Authorization", bearer(outra)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/contatos-confianca/usuario/" + dona.getId())
                        .header("Authorization", bearer(outra)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/usuarios/" + dona.getId())
                        .header("Authorization", bearer(outra)))
                .andExpect(status().isForbidden());
    }

    @Test
    void gerenteNaoPodeVerFichaDeContexto() throws Exception {
        Usuario dona = criar(Perfil.CIDADA);
        Usuario gerente = criar(Perfil.GERENTE);

        mockMvc.perform(get("/api/fichas-contexto/usuario/" + dona.getId())
                        .header("Authorization", bearer(gerente)))
                .andExpect(status().isForbidden());
    }

    @Test
    void salvarFichaDeveFicarPendenteEPublicarEvento() throws Exception {
        Usuario dona = criar(Perfil.CIDADA);

        mockMvc.perform(put("/api/fichas-contexto/usuario/" + dona.getId())
                        .header("Authorization", bearer(dona))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FICHA_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumoStatus").value("PENDENTE"))
                .andExpect(jsonPath("$.numeroBo").value("123/2026"));

        verify(eventoPublisher).publicarFichaAtualizada(any());
    }

    @Test
    void gerarResumoDeveUsarAIaEAcrescentarAvisoDeNaoVerificada() throws Exception {
        Usuario dona = criar(Perfil.CIDADA);
        when(geradorResumo.gerar(any())).thenReturn("- Possível arma informada pela usuária");

        mockMvc.perform(put("/api/fichas-contexto/usuario/" + dona.getId())
                        .header("Authorization", bearer(dona))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FICHA_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/fichas-contexto/usuario/" + dona.getId() + "/resumo")
                        .header("Authorization", bearer(dona)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GERADO"))
                .andExpect(jsonPath("$.resumo").value(startsWith(ResumoFichaService.AVISO)));

        mockMvc.perform(get("/api/fichas-contexto/usuario/" + dona.getId() + "/resumo")
                        .header("Authorization", bearer(dona)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GERADO"));
    }

    @Test
    void falhaDaIaNaoDeveDerrubarARequisicao() throws Exception {
        Usuario dona = criar(Perfil.CIDADA);
        when(geradorResumo.gerar(any())).thenThrow(new IllegalStateException("IA fora do ar"));

        mockMvc.perform(put("/api/fichas-contexto/usuario/" + dona.getId())
                        .header("Authorization", bearer(dona))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(FICHA_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/fichas-contexto/usuario/" + dona.getId() + "/resumo")
                        .header("Authorization", bearer(dona)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALHA"));
    }

    @Test
    void emailRepetidoNoCadastroDeveRetornar409() throws Exception {
        Usuario existente = criar(Perfil.CIDADA);

        String json = """
                {"nome":"Outra","email":"%s","senha":"Senha@1234"}
                """.formatted(existente.getEmail());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
