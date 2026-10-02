package davi.prado.XPTO.dto.conta;

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
