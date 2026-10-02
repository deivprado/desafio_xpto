package davi.prado.XPTO.dto.Cliente;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private String documento;

    private String nome;

    private String email;

    private String telefone;

    private String tipoCliente;

    private LocalDate dataCadastro;

    private String ativo;

    private LocalDate dataNascimento;

    private String nomeFantasia;


}
