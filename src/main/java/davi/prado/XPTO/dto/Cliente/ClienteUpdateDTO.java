package davi.prado.XPTO.dto.Cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {

    private String nome;

    private String email;

    private String telefone;

    private String ativo;

    private LocalDate dataNascimento;

    private String nomeFantasia;
}
