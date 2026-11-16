package br.com.fiap.valky.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Ficha de contexto. Só os dois textos livres são obrigatórios; os campos fixos
 * (agressor, arma, veículo, endereços, BO, medida protetiva) são opcionais.
 */
public record FichaContextoCadastroDTO(
        @NotBlank(message = "As informações importantes são obrigatórias.")
        @Size(max = 2000, message = "As informações importantes devem ter no máximo 2000 caracteres.")
        String informacoesImportantes,

        @NotBlank(message = "As orientações de emergência são obrigatórias.")
        @Size(max = 1000, message = "As orientações de emergência devem ter no máximo 1000 caracteres.")
        String orientacoesEmergencia,

        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nomeAgressor,

        @Size(max = 60, message = "A relação deve ter no máximo 60 caracteres.")
        String relacaoAgressor,

        Boolean possuiArma,

        @Size(max = 200, message = "O veículo deve ter no máximo 200 caracteres.")
        String veiculo,

        @Size(max = 500, message = "Os endereços devem ter no máximo 500 caracteres.")
        String enderecosConhecidos,

        @Size(max = 40, message = "O número do BO deve ter no máximo 40 caracteres.")
        String numeroBo,

        Boolean medidaProtetiva) {
}
