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
}
