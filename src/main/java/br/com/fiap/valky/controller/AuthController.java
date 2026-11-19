package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.LoginDTO;
import br.com.fiap.valky.dto.TokenRespostaDTO;
import br.com.fiap.valky.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login: devolve o token JWT (use como Bearer nos demais endpoints)")
    @PostMapping("/login")
    public TokenRespostaDTO login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
}
