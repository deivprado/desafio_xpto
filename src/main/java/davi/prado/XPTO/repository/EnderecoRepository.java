package davi.prado.XPTO.repository;

import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {

    @Query(value = "SELECT e FROM EnderecoEntity e WHERE e.cep = :cep AND e.numero = :numero AND e.cliente = :clienteId")
    Optional<EnderecoEntity> findByUnique(@Param("cep") String cep, @Param("numero") String numero, @Param("clienteId") Long clienteId );

    @Query(value = "SELECT e FROM EnderecoEntity e WHERE e.cep = :cep AND e.numero = :numero AND e.cliente = :clienteId AND e.ativo = 'S'" )
    Optional<EnderecoEntity> findByUniqueAtivo(@Param("cep") String cep, @Param("numero") String numero, @Param("clienteId") Long clienteId );


}
