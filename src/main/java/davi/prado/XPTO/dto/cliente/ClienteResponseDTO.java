package davi.prado.XPTO.dto.cliente;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private LocalDateTime dataCadastro;

    private String ativo;

    private LocalDate dataNascimento;

    private String nomeFantasia;


}
