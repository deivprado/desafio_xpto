package davi.prado.XPTO.console;

import davi.prado.XPTO.dto.movimentacao.MovimentacaoCreateDTO;
import davi.prado.XPTO.dto.movimentacao.MovimentacaoResponseDTO;
import davi.prado.XPTO.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Menu de movimentações: lança uma movimentação na conta de um cliente.
 * É a versão "manual" da simulação de integração com a instituição financeira.
 */
@Component
@RequiredArgsConstructor
public class MovimentacaoMenu {

    private final MovimentacaoService movimentacaoService;

    public void abrir() {
        int opcao;

        do {
            System.out.println("\n--- MOVIMENTAÇÕES ---");
            System.out.println(" 1 - Lançar movimentação");
            System.out.println(" 0 - Voltar");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            try {
                switch (opcao) {
                    case 1 -> lancar();
                    case 0 -> { }
                    default -> System.out.println("\nOpção inválida!");
                }
            } catch (RuntimeException e) {
                System.out.println("\n>>> Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void lancar() {
        System.out.println("\n--- Nova movimentação ---");
        String tipo = ConsoleUtil.lerTexto("Tipo (CREDITO ou DEBITO): ").toUpperCase();
        BigDecimal valor = ConsoleUtil.lerValor("Valor: ");
        String descricao = ConsoleUtil.lerTexto("Descrição: ");

        System.out.println("--- Conta ---");
        String instituicao = ConsoleUtil.lerTexto("Instituição financeira: ");
        String agencia = ConsoleUtil.lerTexto("Agência: ");
        String numeroConta = ConsoleUtil.lerTexto("Número da conta: ");

        MovimentacaoResponseDTO movimentacao = movimentacaoService.criarMovimentacao(MovimentacaoCreateDTO.builder()
                .tipoMovimentacao(tipo)
                .valor(valor)
                .descricao(descricao)
                .contaInstituicao(instituicao)
                .contaAgencia(agencia)
                .contaNumero(numeroConta)
                .build());

        System.out.println("\nMovimentação registrada!");
        System.out.println(" " + movimentacao.getTipoMovimentacao()
                + " | " + ConsoleUtil.dinheiro(movimentacao.getValor())
                + " | " + movimentacao.getDescricao()
                + " | " + movimentacao.getContaInstituicao()
                + " ag " + movimentacao.getContaAgencia()
                + " conta " + movimentacao.getContaNumero()
                + " | " + ConsoleUtil.dataHora(movimentacao.getDataMovimentacao()));
    }
}
