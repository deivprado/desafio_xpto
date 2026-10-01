package davi.prado.XPTO.dto;

import davi.prado.XPTO.entity.ClienteEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {

    @NotNull
    private String instituicaoFinanceira;

    @NotNull
    private String agencia;

    @NotNull
    private String numeroConta;

    @NotNull
    private String ativo;

    @NotNull
    private Long clienteId;
}
