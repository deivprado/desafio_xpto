package davi.prado.XPTO.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CLIENTE")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 14, unique = true)
    private String documento;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 2)
    private String tipoCliente;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false, length = 1)
    private String ativo;

    private LocalDate dataNascimento;

    private String nomeFantasia;
}
