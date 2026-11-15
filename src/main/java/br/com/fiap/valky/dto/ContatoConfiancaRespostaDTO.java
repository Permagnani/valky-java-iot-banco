package br.com.fiap.valky.dto;

public class ContatoConfiancaRespostaDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String parentesco;

    public ContatoConfiancaRespostaDTO(
            Long id,
            String nome,
            String telefone,
            String parentesco) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.parentesco = parentesco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getParentesco() {
        return parentesco;
    }
}