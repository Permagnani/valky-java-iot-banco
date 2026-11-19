package br.com.fiap.valky.entity;

/**
 * Papéis de acesso da API. No JWT eles viajam na claim "roles" e o Spring
 * Security os transforma em ROLE_CIDADA / ROLE_GERENTE.
 */
public enum Perfil {
    CIDADA,
    GERENTE
}
