package davi.prado.XPTO.repository;

import davi.prado.XPTO.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Query(value = "SELECT c FROM ClienteEntity c WHERE c.documento = :documento AND c.ativo = 'S'")
    Optional<ClienteEntity> findByDocumento(@Param("documento") String documento);

}
