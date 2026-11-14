
package br.com.fiap.valky;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.Usuario;
import br.com.fiap.valky.repository.FichaContextoRepository;
import br.com.fiap.valky.repository.UsuarioRepository;
import br.com.fiap.valky.service.FichaContextoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FichaContextoServiceTest {

    @Mock
    private FichaContextoRepository fichaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FichaContextoService fichaService;

    @Test
    void deveAtualizarFichaExistenteSemCriarOutra() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        FichaContexto fichaExistente = new FichaContexto();
        fichaExistente.setUsuario(usuario);
        fichaExistente.setInformacoesImportantes("Informação antiga");

        FichaContexto fichaAtualizada = new FichaContexto();
        fichaAtualizada.setInformacoesImportantes("Informação atualizada");
        fichaAtualizada.setOrientacoesEmergencia("Orientação atualizada");

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

        org.junit.jupiter.api.Assertions.assertEquals(
                "Informação atualizada",
                resultado.getInformacoesImportantes()
        );
    }
}
