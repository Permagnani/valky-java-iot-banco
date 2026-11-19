package br.com.fiap.valky.repository;

import br.com.fiap.valky.entity.ContatoConfianca;
import br.com.fiap.valky.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContatoConfiancaRepository
        extends JpaRepository<ContatoConfianca, Long> {

    List<ContatoConfianca> findByUsuario(Usuario usuario);

    Optional<ContatoConfianca> findByIdAndUsuarioId(Long id, Long usuarioId);
}
