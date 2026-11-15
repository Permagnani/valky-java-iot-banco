package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.ContatoConfiancaCadastroDTO;
import br.com.fiap.valky.dto.ContatoConfiancaRespostaDTO;
import br.com.fiap.valky.entity.ContatoConfianca;
import br.com.fiap.valky.service.ContatoConfiancaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contatos-confianca")
public class ContatoConfiancaController {

    private final ContatoConfiancaService contatoService;

    public ContatoConfiancaController(ContatoConfiancaService contatoService) {
        this.contatoService = contatoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ContatoConfiancaRespostaDTO> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return contatoService.listarPorUsuario(usuarioId)
                .stream()
                .map(contato -> new ContatoConfiancaRespostaDTO(
                        contato.getId(),
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getParentesco()
                ))
                .toList();
    }

    @PostMapping("/usuario/{usuarioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ContatoConfiancaRespostaDTO cadastrar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody ContatoConfiancaCadastroDTO dto) {

        ContatoConfianca contato = new ContatoConfianca();
        contato.setNome(dto.getNome());
        contato.setTelefone(dto.getTelefone());
        contato.setParentesco(dto.getParentesco());

        ContatoConfianca salvo = contatoService.cadastrar(usuarioId, contato);

        return new ContatoConfiancaRespostaDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getTelefone(),
                salvo.getParentesco()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        contatoService.excluir(id);
    }
}