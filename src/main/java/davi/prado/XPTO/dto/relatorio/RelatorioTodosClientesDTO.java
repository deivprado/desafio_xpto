package davi.prado.XPTO.dto.relatorio;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Uma linha do relatório de saldo de todos os clientes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioTodosClientesDTO {

    private String nomeCliente;

    private LocalDateTime dataCadastro;

    private BigDecimal saldo;
}
