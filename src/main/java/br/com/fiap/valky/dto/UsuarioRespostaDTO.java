package br.com.fiap.valky.dto;

import java.time.LocalDateTime;

public class UsuarioRespostaDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime dataCriacao;

    public UsuarioRespostaDTO(
            Long id,
            String nome,
            String email,
            String telefone,
            LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}