package davi.prado.XPTO.dto.movimentacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoCreateDTO {

    @NotBlank
    private String tipoMovimentacao;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotBlank
    private String descricao;

    @NotBlank
    private String contaInstituicao;

    @NotBlank
    private String contaNumero;

    @NotBlank
    private String contaAgencia;

}
