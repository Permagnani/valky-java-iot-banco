package br.com.fiap.valky.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContatoConfiancaCadastroDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,

        @NotBlank(message = "O parentesco é obrigatório.")
        @Size(max = 50, message = "O parentesco deve ter no máximo 50 caracteres.")
        String parentesco) {
}
