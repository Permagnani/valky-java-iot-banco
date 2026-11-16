package br.com.fiap.valky.dto;

public record TokenRespostaDTO(
        String token,
        String tipo,
        long expiraEmSegundos,
        Long usuarioId,
        String nome,
        String perfil) {
}
