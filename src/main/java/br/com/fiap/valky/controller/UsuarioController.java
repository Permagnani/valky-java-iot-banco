package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.UsuarioAtualizacaoDTO;
import br.com.fiap.valky.dto.UsuarioCadastroDTO;
import br.com.fiap.valky.dto.UsuarioRespostaDTO;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.security.AcessoService;
import br.com.fiap.valky.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AcessoService acessoService;

    public UsuarioController(UsuarioService usuarioService, AcessoService acessoService) {
        this.usuarioService = usuarioService;
        this.acessoService = acessoService;
    }

    @Operation(summary = "Cadastro público (cria sempre uma conta CIDADA)")
    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        Usuario usuario = usuarioService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioRespostaDTO.de(usuario));
    }

    @Operation(summary = "Lista paginada de usuários (somente GERENTE)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<Page<UsuarioRespostaDTO>> listarTodos(Pageable pageable) {

        return ResponseEntity.ok(
                usuarioService.listarTodos(pageable).map(UsuarioRespostaDTO::de));
    }

    @Operation(summary = "Busca por id (a própria usuária ou GERENTE)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> buscarPorId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        acessoService.exigirDonaOuGerente(jwt, id);

        return ResponseEntity.ok(UsuarioRespostaDTO.de(usuarioService.buscarPorId(id)));
    }

    @Operation(summary = "Atualiza os dados da própria conta",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> atualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO dto) {

        acessoService.exigirDona(jwt, id);

        Usuario dados = new Usuario();
        dados.setNome(dto.nome());
        dados.setEmail(dto.email());
        dados.setTelefone(dto.telefone());

        return ResponseEntity.ok(UsuarioRespostaDTO.de(usuarioService.atualizar(id, dados)));
    }

    @Operation(summary = "Exclui a conta (a própria usuária ou GERENTE)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        acessoService.exigirDonaOuGerente(jwt, id);

        usuarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
