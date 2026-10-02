package davi.prado.XPTO.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreateDTO {

    @NotBlank
    private String documento;
    @NotBlank
    private String nome;
    @NotBlank
    private String email;
    @NotBlank
    private String telefone;

    private LocalDate dataNascimento;

    private String nomeFantasia;

    private String instituicaoFinanceira;

    private String agencia;

    private String numeroConta;

    private BigDecimal valorInicial;
}
