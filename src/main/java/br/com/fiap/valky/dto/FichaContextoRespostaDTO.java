package br.com.fiap.valky.dto;

import java.time.LocalDateTime;

public class FichaContextoRespostaDTO {

    private Long id;
    private String informacoesImportantes;
    private String orientacoesEmergencia;
    private LocalDateTime dataAtualizacao;

    public FichaContextoRespostaDTO(
            Long id,
            String informacoesImportantes,
            String orientacoesEmergencia,
            LocalDateTime dataAtualizacao) {
        this.id = id;
        this.informacoesImportantes = informacoesImportantes;
        this.orientacoesEmergencia = orientacoesEmergencia;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public String getInformacoesImportantes() {
        return informacoesImportantes;
    }

    public String getOrientacoesEmergencia() {
        return orientacoesEmergencia;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}