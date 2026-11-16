
package br.com.fiap.valky;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.messaging.FichaEventoPublisher;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import br.com.fiap.valky.service.FichaContextoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaContextoServiceTest {

    @Mock
    private FichaContextoRepository fichaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FichaEventoPublisher eventoPublisher;

    @InjectMocks
    private FichaContextoService fichaService;

    @Test
    void deveAtualizarFichaExistenteSemCriarOutra() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        FichaContexto fichaExistente = new FichaContexto();
        fichaExistente.setId(10L);
        fichaExistente.setUsuario(usuario);
        fichaExistente.setInformacoesImportantes("Informação antiga");
        fichaExistente.setResumoIa("resumo antigo");
        fichaExistente.setResumoStatus(ResumoStatus.GERADO);

        FichaContexto fichaAtualizada = new FichaContexto();
        fichaAtualizada.setInformacoesImportantes("Informação atualizada");
        fichaAtualizada.setOrientacoesEmergencia("Orientação atualizada");
        fichaAtualizada.setPossuiArma(true);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(fichaRepository.findByUsuario(usuario))
                .thenReturn(Optional.of(fichaExistente));

        when(fichaRepository.save(any(FichaContexto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FichaContexto resultado =
                fichaService.salvar(1L, fichaAtualizada);

        assertSame(fichaExistente, resultado);

        verify(fichaRepository).save(fichaExistente);
        verify(fichaRepository, times(1)).save(any(FichaContexto.class));

        assertEquals("Informação atualizada", resultado.getInformacoesImportantes());
        assertEquals(Boolean.TRUE, resultado.getPossuiArma());
    }

    @Test
    void deveInvalidarResumoAntigoEPublicarEventoAoSalvar() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        FichaContexto fichaExistente = new FichaContexto();
        fichaExistente.setId(10L);
        fichaExistente.setResumoIa("resumo antigo");
        fichaExistente.setResumoStatus(ResumoStatus.GERADO);

        FichaContexto nova = new FichaContexto();
        nova.setInformacoesImportantes("x");
        nova.setOrientacoesEmergencia("y");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(fichaRepository.findByUsuario(usuario)).thenReturn(Optional.of(fichaExistente));
        when(fichaRepository.save(any(FichaContexto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FichaContexto resultado = fichaService.salvar(1L, nova);

        assertNull(resultado.getResumoIa());
        assertEquals(ResumoStatus.PENDENTE, resultado.getResumoStatus());
        verify(eventoPublisher).publicarFichaAtualizada(10L);
    }
}
