package br.com.fiap.valky.dto;

import br.com.fiap.valky.entity.FichaContexto;

import java.time.LocalDateTime;

public record ResumoFichaRespostaDTO(
        String status,
        String resumo,
        LocalDateTime geradoEm) {

    public static ResumoFichaRespostaDTO de(FichaContexto f) {
        return new ResumoFichaRespostaDTO(
                f.getResumoStatus() == null ? null : f.getResumoStatus().name(),
                f.getResumoIa(),
                f.getResumoGeradoEm());
    }
}
