package davi.prado.XPTO.dto.relatorio;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioReceitaXptoDTO {

    private LocalDate periodoInicio;

    private LocalDate periodoFim;

    private List<RelatorioReceitaClienteDTO> clientes;

    private BigDecimal totalReceitas;
}
