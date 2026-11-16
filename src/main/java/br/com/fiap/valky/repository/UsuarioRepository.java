package br.com.fiap.valky.repository;

import br.com.fiap.valky.entity.Perfil;
import br.com.fiap.valky.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByPerfil(Perfil perfil);
}
