package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.ContatoConfiancaCadastroDTO;
import br.com.fiap.valky.dto.ContatoConfiancaRespostaDTO;
import br.com.fiap.valky.entity.ContatoConfianca;
import br.com.fiap.valky.security.AcessoService;
import br.com.fiap.valky.service.ContatoConfiancaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contatos-confianca")
@Tag(name = "Contatos de confiança")
@SecurityRequirement(name = "bearerAuth")
public class ContatoConfiancaController {

    private final ContatoConfiancaService contatoService;
    private final AcessoService acessoService;

    public ContatoConfiancaController(ContatoConfiancaService contatoService,
                                      AcessoService acessoService) {
        this.contatoService = contatoService;
        this.acessoService = acessoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ContatoConfiancaRespostaDTO> listarPorUsuario(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId) {

        acessoService.exigirDona(jwt, usuarioId);

        return contatoService.listarPorUsuario(usuarioId)
                .stream()
                .map(ContatoConfiancaRespostaDTO::de)
                .toList();
    }

    @PostMapping("/usuario/{usuarioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ContatoConfiancaRespostaDTO cadastrar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId,
            @Valid @RequestBody ContatoConfiancaCadastroDTO dto) {

        acessoService.exigirDona(jwt, usuarioId);

        ContatoConfianca contato = new ContatoConfianca();
        contato.setNome(dto.nome());
        contato.setTelefone(dto.telefone());
        contato.setParentesco(dto.parentesco());

        return ContatoConfiancaRespostaDTO.de(contatoService.cadastrar(usuarioId, contato));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        contatoService.excluir(id, acessoService.usuarioId(jwt));
    }
}
