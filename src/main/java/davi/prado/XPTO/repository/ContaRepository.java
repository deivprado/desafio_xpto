package davi.prado.XPTO.repository;

import davi.prado.XPTO.entity.ContaEntity;
import davi.prado.XPTO.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<ContaEntity, Long> {

    @Query(value = "SELECT c FROM ContaEntity c WHERE UPPER(c.instituicaoFinanceira) = UPPER(:instituicao) AND c.agencia = :agencia AND c.numeroConta = :numero")
    Optional<ContaEntity> findByUnique(@Param("instituicao") String instituicao, @Param("agencia") String agencia, @Param("numero") String numero);
}
