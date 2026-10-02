package davi.prado.XPTO.dto.relatorio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioReceitaClienteDTO {

    private String nomeCliente;

    private Long quantidadeMovimentacoes;

    private BigDecimal valorReceita;
}
