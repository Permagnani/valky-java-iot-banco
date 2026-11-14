package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.UsuarioCadastroDTO;
import br.com.fiap.valky.dto.UsuarioRespostaDTO;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}