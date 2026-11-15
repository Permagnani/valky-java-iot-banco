package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.FichaContextoCadastroDTO;
import br.com.fiap.valky.dto.FichaContextoRespostaDTO;
import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.service.FichaContextoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fichas-contexto")
public class FichaContextoController {

    private final FichaContextoService fichaService;

    public FichaContextoController(FichaContextoService fichaService) {
        this.fichaService = fichaService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<FichaContextoRespostaDTO> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        FichaContexto ficha = fichaService.buscarPorUsuario(usuarioId);

        if (ficha == null) {
            return ResponseEntity.notFound().build();
        }

        FichaContextoRespostaDTO resposta = new FichaContextoRespostaDTO(
                ficha.getId(),
                ficha.getInformacoesImportantes(),
                ficha.getOrientacoesEmergencia(),
                ficha.getDataAtualizacao()
        );

        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/usuario/{usuarioId}")
    public ResponseEntity<FichaContextoRespostaDTO> salvarOuAtualizar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody FichaContextoCadastroDTO dto) {

        FichaContexto ficha = new FichaContexto();
        ficha.setInformacoesImportantes(dto.getInformacoesImportantes());
        ficha.setOrientacoesEmergencia(dto.getOrientacoesEmergencia());

        FichaContexto salva = fichaService.salvar(usuarioId, ficha);

        FichaContextoRespostaDTO resposta = new FichaContextoRespostaDTO(
                salva.getId(),
                salva.getInformacoesImportantes(),
                salva.getOrientacoesEmergencia(),
                salva.getDataAtualizacao()
        );

        return ResponseEntity.ok(resposta);
    }
}