package br.com.fiap.valky.controller;

import br.com.fiap.valky.dto.FichaContextoCadastroDTO;
import br.com.fiap.valky.dto.FichaContextoRespostaDTO;
import br.com.fiap.valky.dto.ResumoFichaRespostaDTO;
import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.security.AcessoService;
import br.com.fiap.valky.service.FichaContextoService;
import br.com.fiap.valky.service.ResumoFichaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/** A ficha é sensível: só a própria usuária acessa (nem o GERENTE). */
@RestController
@RequestMapping("/api/fichas-contexto")
@Tag(name = "Ficha de contexto")
@SecurityRequirement(name = "bearerAuth")
public class FichaContextoController {

    private final FichaContextoService fichaService;
    private final ResumoFichaService resumoService;
    private final AcessoService acessoService;

    public FichaContextoController(FichaContextoService fichaService,
                                   ResumoFichaService resumoService,
                                   AcessoService acessoService) {
        this.fichaService = fichaService;
        this.resumoService = resumoService;
        this.acessoService = acessoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<FichaContextoRespostaDTO> buscarPorUsuario(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId) {

        acessoService.exigirDona(jwt, usuarioId);

        FichaContexto ficha = fichaService.buscarPorUsuario(usuarioId);

        if (ficha == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(FichaContextoRespostaDTO.de(ficha));
    }

    @Operation(summary = "Cria/atualiza a ficha e publica o evento que gera o resumo por IA")
    @PutMapping("/usuario/{usuarioId}")
    public ResponseEntity<FichaContextoRespostaDTO> salvarOuAtualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId,
            @Valid @RequestBody FichaContextoCadastroDTO dto) {

        acessoService.exigirDona(jwt, usuarioId);

        FichaContexto ficha = new FichaContexto();
        ficha.setInformacoesImportantes(dto.informacoesImportantes());
        ficha.setOrientacoesEmergencia(dto.orientacoesEmergencia());
        ficha.setNomeAgressor(dto.nomeAgressor());
        ficha.setRelacaoAgressor(dto.relacaoAgressor());
        ficha.setPossuiArma(dto.possuiArma());
        ficha.setVeiculo(dto.veiculo());
        ficha.setEnderecosConhecidos(dto.enderecosConhecidos());
        ficha.setNumeroBo(dto.numeroBo());
        ficha.setMedidaProtetiva(dto.medidaProtetiva());

        return ResponseEntity.ok(
                FichaContextoRespostaDTO.de(fichaService.salvar(usuarioId, ficha)));
    }

    @Operation(summary = "Consulta o resumo da ficha gerado pela IA (PENDENTE, GERADO ou FALHA)")
    @GetMapping("/usuario/{usuarioId}/resumo")
    public ResumoFichaRespostaDTO buscarResumo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId) {

        acessoService.exigirDona(jwt, usuarioId);

        return ResumoFichaRespostaDTO.de(fichaExistente(usuarioId));
    }

    @Operation(summary = "Gera o resumo agora, de forma síncrona (útil se a fila estiver fora do ar)")
    @PostMapping("/usuario/{usuarioId}/resumo")
    public ResumoFichaRespostaDTO gerarResumo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long usuarioId) {

        acessoService.exigirDona(jwt, usuarioId);

        FichaContexto ficha = fichaExistente(usuarioId);

        return ResumoFichaRespostaDTO.de(resumoService.gerarParaFicha(ficha.getId()));
    }

    private FichaContexto fichaExistente(Long usuarioId) {
        FichaContexto ficha = fichaService.buscarPorUsuario(usuarioId);

        if (ficha == null) {
            throw new RecursoNaoEncontradoException("A usuária ainda não preencheu a ficha de contexto.");
        }

        return ficha;
    }
}
