package br.com.fiap.valky.entity;

/** Situação do resumo gerado pela IA para a ficha de contexto. */
public enum ResumoStatus {
    /** Ficha salva; resumo ainda na fila ou sendo gerado. */
    PENDENTE,
    GERADO,
    /** A IA não respondeu (chave inválida, limite de requisições, fora do ar...). */
    FALHA
}
