package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.EstatisticasDTO;
import br.com.fiap.valky.service.GestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Área do perfil GERENTE (protegida em SecurityConfig: /api/gestao/** exige ROLE_GERENTE). */
@RestController
@RequestMapping("/api/gestao")
@Tag(name = "Gestão (GERENTE)")
@SecurityRequirement(name = "bearerAuth")
public class GestaoController {

    private final GestaoService gestaoService;

    public GestaoController(GestaoService gestaoService) {
        this.gestaoService = gestaoService;
    }

    @Operation(summary = "Números agregados da plataforma (sem dados pessoais)")
    @GetMapping("/estatisticas")
    public EstatisticasDTO estatisticas() {
        return gestaoService.estatisticas();
    }
}
