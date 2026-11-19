
package br.com.fiap.valky;

import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.security.JwtService;
import br.com.fiap.valky.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    private String bearer(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste " + perfil);
        usuario.setEmail(UUID.randomUUID() + "@teste.com");
        usuario.setSenha("Senha@1234");
        usuario.setPerfil(perfil);

        return "Bearer " + jwtService.gerarToken(usuarioService.cadastrar(usuario));
    }

    @Test
    void deveRetornar400QuandoDadosDoCadastroForemInvalidos()
            throws Exception {

        String json = """
                {
                    "nome": "",
                    "email": "email-invalido",
                    "senha": "123",
                    "telefone": "11999999999"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.email").exists())
                .andExpect(jsonPath("$.campos.senha").exists());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistirParaOGerente() throws Exception {

        mockMvc.perform(get("/api/usuarios/999999")
                        .header("Authorization", bearer(Perfil.GERENTE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado."));
    }

    @Test
    void deveRetornar401QuandoNaoHouverToken() throws Exception {

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoTokenForInvalido() throws Exception {

        mockMvc.perform(get("/api/usuarios/1")
                        .header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }
}
