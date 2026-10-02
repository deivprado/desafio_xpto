package davi.prado.XPTO.dto.conta;

import jakarta.validation.constraints.NotBlank;
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
