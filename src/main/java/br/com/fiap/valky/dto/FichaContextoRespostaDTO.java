package br.com.fiap.valky.dto;

import br.com.fiap.valky.entity.FichaContexto;

import java.time.LocalDateTime;

public record FichaContextoRespostaDTO(
        Long id,
        String informacoesImportantes,
        String orientacoesEmergencia,
        String nomeAgressor,
        String relacaoAgressor,
        Boolean possuiArma,
        String veiculo,
        String enderecosConhecidos,
        String numeroBo,
        Boolean medidaProtetiva,
        String resumoStatus,
        String resumoIa,
        LocalDateTime dataAtualizacao) {

    public static FichaContextoRespostaDTO de(FichaContexto f) {
        return new FichaContextoRespostaDTO(
                f.getId(),
                f.getInformacoesImportantes(),
                f.getOrientacoesEmergencia(),
                f.getNomeAgressor(),
                f.getRelacaoAgressor(),
                f.getPossuiArma(),
                f.getVeiculo(),
                f.getEnderecosConhecidos(),
                f.getNumeroBo(),
                f.getMedidaProtetiva(),
                f.getResumoStatus() == null ? null : f.getResumoStatus().name(),
                f.getResumoIa(),
                f.getDataAtualizacao());
    }
}
