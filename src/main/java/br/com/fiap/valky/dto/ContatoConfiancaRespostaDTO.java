package br.com.fiap.valky.dto;

import br.com.fiap.valky.entity.ContatoConfianca;

public record ContatoConfiancaRespostaDTO(
        Long id,
        String nome,
        String telefone,
        String parentesco) {

    public static ContatoConfiancaRespostaDTO de(ContatoConfianca contato) {
        return new ContatoConfiancaRespostaDTO(
                contato.getId(),
                contato.getNome(),
                contato.getTelefone(),
                contato.getParentesco());
    }
}
