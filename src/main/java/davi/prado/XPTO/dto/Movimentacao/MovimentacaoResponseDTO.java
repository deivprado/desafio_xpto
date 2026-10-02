package davi.prado.XPTO.dto.Movimentacao;

import davi.prado.XPTO.entity.ContaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoResponseDTO {

    private String tipoMovimentacao;

    private BigDecimal valor;

    private LocalDateTime dataMovimentacao;

    private String descricao;

    private String contaInstituicao;

    private String contaNumero;

    private String contaAgencia;

}
