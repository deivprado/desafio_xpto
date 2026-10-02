package davi.prado.XPTO.dto.Conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaCreateDTO {

    @NotBlank
    private String instituicaoFinanceira;

    @NotBlank
    private String agencia;

    @NotBlank
    private String numeroConta;

    @NotNull
    private String clienteDocumento;

}
