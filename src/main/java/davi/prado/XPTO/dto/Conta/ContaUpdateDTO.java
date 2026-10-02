package davi.prado.XPTO.dto.Conta;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaUpdateDTO {

    private String instituicaoFinanceira;

    private String agencia;

    private String numeroConta;

}
