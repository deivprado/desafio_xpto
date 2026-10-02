package davi.prado.XPTO.dto.relatorio;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioSaldoClienteDTO {

    private String nome;

    private LocalDateTime dataCadastro;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String uf;

    private String cep;

    private Long qtdCredito;

    private Long qtdDebito;

    private Long totalMovimentacoes;

    private BigDecimal valorPagoMovimentacoes;

    private BigDecimal saldoInicial;

    private BigDecimal saldoAtual;
}
