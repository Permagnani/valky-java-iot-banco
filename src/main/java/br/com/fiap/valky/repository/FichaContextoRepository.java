
package br.com.fiap.valky.repository;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichaContextoRepository
        extends JpaRepository<FichaContexto, Long> {

    Optional<FichaContexto> findByUsuario(Usuario usuario);
}
