package br.com.fiap.valky.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FichaContextoCadastroDTO {

    @NotBlank(message = "As informações importantes são obrigatórias.")
    @Size(max = 2000)
    private String informacoesImportantes;

    @NotBlank(message = "As orientações de emergência são obrigatórias.")
    @Size(max = 1000)
    private String orientacoesEmergencia;

    public String getInformacoesImportantes() {
        return informacoesImportantes;
    }

    public void setInformacoesImportantes(String informacoesImportantes) {
        this.informacoesImportantes = informacoesImportantes;
    }

    public String getOrientacoesEmergencia() {
        return orientacoesEmergencia;
    }

    public void setOrientacoesEmergencia(String orientacoesEmergencia) {
        this.orientacoesEmergencia = orientacoesEmergencia;
    }
}