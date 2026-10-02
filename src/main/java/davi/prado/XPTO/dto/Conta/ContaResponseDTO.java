package davi.prado.XPTO.dto.Conta;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaResponseDTO {

    private String instituicaoFinanceira;

    private String agencia;

    private String numeroConta;

    private String ativo;

    private String clienteDocumento;
}
