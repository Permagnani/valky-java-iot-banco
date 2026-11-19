package br.com.fiap.valky.ia;

import br.com.fiap.valky.entity.FichaContexto;

/** Porta para o LLM. Existe para o resto do sistema (e os testes) não depender do Spring AI. */
public interface GeradorResumo {

    /** Devolve um resumo curto da ficha, em português. */
    String gerar(FichaContexto ficha);
}
