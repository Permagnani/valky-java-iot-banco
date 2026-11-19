package br.com.fiap.valky.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cadastro público. O perfil NÃO vem do cliente: toda conta nova é CIDADA. */
public record UsuarioCadastroDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
        String senha,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone) {
}
