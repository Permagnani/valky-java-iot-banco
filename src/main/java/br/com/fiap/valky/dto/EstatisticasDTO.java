package br.com.fiap.valky.dto;

public record EstatisticasDTO(
        long totalUsuarios,
        long totalCidadas,
        long totalGerentes,
        long totalContatosConfianca,
        long totalFichas,
        long fichasComResumoGerado) {
}
