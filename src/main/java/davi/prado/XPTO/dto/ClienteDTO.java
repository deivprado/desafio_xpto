package davi.prado.XPTO.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    @NotNull
    private String nome;

    @Column(nullable = false, length = 14, unique = true)
    private String documento;

    @NotNull
    private String telefone;

    @NotNull
    private String email;

    private LocalDate dataNascimento;

    private String nomeFantasia;
}
