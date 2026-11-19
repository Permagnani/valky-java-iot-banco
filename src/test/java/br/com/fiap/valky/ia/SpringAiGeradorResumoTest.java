package br.com.fiap.valky.ia;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringAiGeradorResumoTest {

    @Test
    void mensagemDeveTrazerSoOsDadosPreenchidos() {
        FichaContexto ficha = new FichaContexto();
        ficha.setNomeAgressor("Fulano de Tal");
        ficha.setPossuiArma(true);
        ficha.setVeiculo("Gol preto ABC1D23");
        ficha.setInformacoesImportantes("Ele conhece meu trabalho.");

        String mensagem = SpringAiGeradorResumo.montarMensagem(ficha);

        assertTrue(mensagem.contains("Fulano de Tal"));
        assertTrue(mensagem.contains("Possui arma: sim"));
        assertTrue(mensagem.contains("ABC1D23"));
        assertFalse(mensagem.contains("Número do BO"));
        assertFalse(mensagem.contains("Medida protetiva"));
    }

    @Test
    void mensagemNaoDeveConterDadosPessoaisDaUsuaria() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria da Silva");
        usuario.setEmail("maria@exemplo.com");
        usuario.setTelefone("11999998888");

        FichaContexto ficha = new FichaContexto();
        ficha.setUsuario(usuario);
        ficha.setInformacoesImportantes("Texto qualquer.");

        String mensagem = SpringAiGeradorResumo.montarMensagem(ficha);

        assertFalse(mensagem.contains("Maria da Silva"));
        assertFalse(mensagem.contains("maria@exemplo.com"));
        assertFalse(mensagem.contains("11999998888"));
    }

    @Test
    void promptDeSistemaDeveProibirChamarAlguemDeCulpado() {
        assertTrue(SpringAiGeradorResumo.PROMPT_SISTEMA.contains("Nunca chame ninguém de culpado"));
        assertTrue(SpringAiGeradorResumo.PROMPT_SISTEMA.contains("não verificado"));
    }
}
