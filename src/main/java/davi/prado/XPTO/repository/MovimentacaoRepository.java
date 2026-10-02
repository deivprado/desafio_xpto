package davi.prado.XPTO.repository;

import davi.prado.XPTO.entity.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEntity, Long> {

    @Query(value = "SELECT m FROM MovimentacaoEntity m WHERE m.conta.id = :contaId")
    List<MovimentacaoEntity> findByConta(@Param("contaId") Long contaId);
}
