package davi.prado.XPTO.dto.Movimentacao;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoCreateDTO {

    private String tipoMovimentacao;

    private BigDecimal valor;

    private String descricao;


}
