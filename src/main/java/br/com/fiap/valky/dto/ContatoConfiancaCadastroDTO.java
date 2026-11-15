package br.com.fiap.valky.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContatoConfiancaCadastroDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 120)
    private String nome;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 20)
    private String telefone;

    @NotBlank(message = "O parentesco é obrigatório.")
    @Size(max = 50)
    private String parentesco;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}