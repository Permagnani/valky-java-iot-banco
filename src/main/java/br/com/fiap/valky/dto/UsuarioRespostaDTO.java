package br.com.fiap.valky.dto;

import br.com.fiap.valky.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioRespostaDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String perfil,
        LocalDateTime dataCriacao) {

    public static UsuarioRespostaDTO de(Usuario usuario) {
        return new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil().name(),
                usuario.getDataCriacao());
    }
}
