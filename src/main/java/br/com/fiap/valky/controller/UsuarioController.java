package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.UsuarioCadastroDTO;
import br.com.fiap.valky.dto.UsuarioRespostaDTO;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        Usuario usuario = usuarioService.cadastrar(dto);

        UsuarioRespostaDTO resposta = new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getDataCriacao()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioRespostaDTO>> listarTodos(
            Pageable pageable) {

        Page<UsuarioRespostaDTO> usuarios =
                usuarioService.listarTodos(pageable)
                        .map(usuario -> new UsuarioRespostaDTO(
                                usuario.getId(),
                                usuario.getNome(),
                                usuario.getEmail(),
                                usuario.getTelefone(),
                                usuario.getDataCriacao()
                        ));

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(usuario -> new UsuarioRespostaDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefone(),
                        usuario.getDataCriacao()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        Usuario dados = new Usuario();
        dados.setNome(dto.getNome());
        dados.setEmail(dto.getEmail());
        dados.setTelefone(dto.getTelefone());

        Usuario atualizado = usuarioService.atualizar(id, dados);

        UsuarioRespostaDTO resposta = new UsuarioRespostaDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone(),
                atualizado.getDataCriacao()
        );

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        usuarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}