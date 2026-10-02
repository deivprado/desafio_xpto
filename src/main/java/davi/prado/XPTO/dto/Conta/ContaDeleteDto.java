package davi.prado.XPTO.dto.Conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaDeleteDto {

    @NotBlank
    private String instituicaoFinanceira;

    @NotBlank
    private String agencia;

    @NotBlank
    private String numeroConta;

}
