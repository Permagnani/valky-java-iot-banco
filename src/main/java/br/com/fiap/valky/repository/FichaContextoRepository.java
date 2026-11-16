package br.com.fiap.valky.repository;

import br.com.fiap.valky.entity.FichaContexto;
import br.com.fiap.valky.entity.ResumoStatus;
import br.com.fiap.valky.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FichaContextoRepository
        extends JpaRepository<FichaContexto, Long> {

    Optional<FichaContexto> findByUsuario(Usuario usuario);

    long countByResumoStatus(ResumoStatus resumoStatus);

    /**
     * Grava o resumo SOMENTE se a ficha continua na mesma versão (dataAtualizacao)
     * que foi enviada para a IA. Assim um resumo velho nunca sobrescreve uma
     * ficha editada enquanto a IA respondia, e as demais colunas não são tocadas.
     *
     * @return 1 se gravou, 0 se a ficha mudou nesse meio tempo
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
            update FichaContexto f
               set f.resumoIa = :resumo,
                   f.resumoStatus = :status,
                   f.resumoGeradoEm = :geradoEm
             where f.id = :id
               and f.dataAtualizacao = :versao
            """)
    int atualizarResumo(@Param("id") Long id,
                        @Param("versao") LocalDateTime versao,
                        @Param("resumo") String resumo,
                        @Param("status") ResumoStatus status,
                        @Param("geradoEm") LocalDateTime geradoEm);
}
