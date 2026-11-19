package br.com.fiap.valky.ia;

import br.com.fiap.valky.entity.FichaContexto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Transforma a ficha de contexto em um resumo curto para a polícia lida rápido.
 *
 * Cuidados deliberados (a ficha é informação da usuária, NÃO é prova):
 *  - o prompt proíbe chamar alguém de culpado/agressor confirmado;
 *  - dados da própria usuária (nome, telefone, e-mail) NÃO são enviados ao LLM;
 *  - o aviso "não verificada" é acrescentado pelo sistema, não depende do modelo.
 */
@Component
public class SpringAiGeradorResumo implements GeradorResumo {

    static final String PROMPT_SISTEMA = """
            Você ajuda uma central de emergência a ler rapidamente informações que uma mulher \
            em situação de risco deixou registradas ANTES de um alerta.

            Regras obrigatórias:
            - Escreva em português do Brasil, em no máximo 6 linhas curtas, em tópicos.
            - Use APENAS o que está nos dados. Nunca invente, deduza ou complete informações.
            - Trate tudo como "risco informado pela usuária, não verificado".
            - Nunca chame ninguém de culpado, criminoso ou agressor confirmado. Use \
            "possível envolvido" ou "pessoa citada".
            - Destaque primeiro o que muda a conduta da equipe: possível arma, veículo e placa, \
            endereços que a pessoa citada conhece, medida protetiva e número do BO.
            - Se um dado não foi informado, simplesmente não o mencione.
            - Não dê conselhos jurídicos nem decida o que a polícia deve fazer.
            """;

    private final ChatClient chatClient;

    public SpringAiGeradorResumo(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(PROMPT_SISTEMA)
                .build();
    }

    @Override
    public String gerar(FichaContexto ficha) {
        return chatClient.prompt()
                .user(montarMensagem(ficha))
                .call()
                .content();
    }

    static String montarMensagem(FichaContexto f) {
        StringBuilder sb = new StringBuilder("Dados registrados pela usuária (não verificados):\n");
        linha(sb, "Possível envolvido", f.getNomeAgressor());
        linha(sb, "Relação com a usuária", f.getRelacaoAgressor());
        linha(sb, "Possui arma", simNao(f.getPossuiArma()));
        linha(sb, "Veículo / placa", f.getVeiculo());
        linha(sb, "Endereços que a pessoa citada conhece", f.getEnderecosConhecidos());
        linha(sb, "Número do BO", f.getNumeroBo());
        linha(sb, "Medida protetiva", simNao(f.getMedidaProtetiva()));
        linha(sb, "Informações gerais (texto da usuária)", f.getInformacoesImportantes());
        linha(sb, "Orientações só para a polícia (texto da usuária)", f.getOrientacoesEmergencia());
        return sb.toString();
    }

    private static void linha(StringBuilder sb, String rotulo, String valor) {
        if (valor != null && !valor.isBlank()) {
            sb.append("- ").append(rotulo).append(": ").append(valor.trim()).append('\n');
        }
    }

    private static String simNao(Boolean valor) {
        if (valor == null) {
            return null;
        }
        return valor ? "sim" : "não";
    }
}
