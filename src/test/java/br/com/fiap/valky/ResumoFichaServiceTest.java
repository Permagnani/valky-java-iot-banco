package br.com.fiap.valky;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.exception.RecursoNaoEncontradoException;
import br.com.fiap.valky.ia.GeradorResumo;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.service.ResumoFichaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumoFichaServiceTest {

    @Mock
    private FichaContextoRepository fichaRepository;

    @Mock
    private GeradorResumo gerador;

    @InjectMocks
    private ResumoFichaService resumoService;

    private FichaContexto fichaComVersao(LocalDateTime versao) {
        FichaContexto ficha = new FichaContexto();
        ficha.setId(5L);
        ficha.setDataAtualizacao(versao);
        return ficha;
    }

    @Test
    void deveGravarResumoComAvisoFixoQuandoAIaResponde() {
        LocalDateTime versao = LocalDateTime.of(2026, 11, 16, 10, 0);
        FichaContexto ficha = fichaComVersao(versao);

        when(fichaRepository.findById(5L)).thenReturn(Optional.of(ficha));
        when(gerador.gerar(ficha)).thenReturn("- Possível arma informada");
        when(fichaRepository.atualizarResumo(
                eq(5L), eq(versao), any(), eq(ResumoStatus.GERADO), any()))
                .thenReturn(1);

        resumoService.gerarParaFicha(5L);

        ArgumentCaptor<String> resumo = ArgumentCaptor.forClass(String.class);
        verify(fichaRepository).atualizarResumo(
                eq(5L), eq(versao), resumo.capture(), eq(ResumoStatus.GERADO), any());

        assertTrue(resumo.getValue().startsWith(ResumoFichaService.AVISO));
        assertTrue(resumo.getValue().contains("Possível arma informada"));
    }

    @Test
    void deveMarcarFalhaQuandoAIaLancaExcecao() {
        LocalDateTime versao = LocalDateTime.of(2026, 11, 16, 10, 0);
        FichaContexto ficha = fichaComVersao(versao);

        when(fichaRepository.findById(5L)).thenReturn(Optional.of(ficha));
        when(gerador.gerar(ficha)).thenThrow(new IllegalStateException("429 Too Many Requests"));
        when(fichaRepository.atualizarResumo(
                eq(5L), eq(versao), isNull(), eq(ResumoStatus.FALHA), any()))
                .thenReturn(1);

        resumoService.gerarParaFicha(5L);

        verify(fichaRepository).atualizarResumo(
                eq(5L), eq(versao), isNull(), eq(ResumoStatus.FALHA), any());
    }

    @Test
    void deveTratarRespostaVaziaDaIaComoFalha() {
        LocalDateTime versao = LocalDateTime.of(2026, 11, 16, 10, 0);
        FichaContexto ficha = fichaComVersao(versao);

        when(fichaRepository.findById(5L)).thenReturn(Optional.of(ficha));
        when(gerador.gerar(ficha)).thenReturn("   ");
        when(fichaRepository.atualizarResumo(
                eq(5L), eq(versao), isNull(), eq(ResumoStatus.FALHA), any()))
                .thenReturn(1);

        resumoService.gerarParaFicha(5L);

        verify(fichaRepository).atualizarResumo(
                eq(5L), eq(versao), isNull(), eq(ResumoStatus.FALHA), any());
    }

    @Test
    void deveLancarNaoEncontradoQuandoFichaNaoExiste() {
        when(fichaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> resumoService.gerarParaFicha(99L));

        verify(gerador, never()).gerar(any());
    }
}
