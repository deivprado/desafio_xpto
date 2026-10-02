package davi.prado.XPTO.console;

import davi.prado.XPTO.dto.relatorio.RelatorioReceitaClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioReceitaXptoDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioSaldoClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioTodosClientesDTO;
import davi.prado.XPTO.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Menu de relatórios (imprime no console, que é uma das saídas permitidas pelo desafio).
 */
@Component
@RequiredArgsConstructor
public class RelatorioMenu {

    private final RelatorioService relatorioService;

    public void abrir() {
        int opcao;

        do {
            System.out.println("\n--- RELATÓRIOS ---");
            System.out.println(" 1 - Saldo do cliente");
            System.out.println(" 2 - Saldo do cliente por período");
            System.out.println(" 3 - Receita da empresa (XPTO) por período");
            System.out.println(" 4 - Saldo de todos os clientes");
            System.out.println(" 0 - Voltar");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            try {
                switch (opcao) {
                    case 1 -> saldoCliente();
                    case 2 -> saldoClientePorPeriodo();
                    case 3 -> receitaXpto();
                    case 4 -> todosClientes();
                    case 0 -> { }
                    default -> System.out.println("\nOpção inválida!");
                }
            } catch (RuntimeException e) {
                System.out.println("\n>>> Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void saldoCliente() {
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");

        RelatorioSaldoClienteDTO relatorio = relatorioService.gerarRelatorioSaldoCliente(documento);

        imprimirSaldo(relatorio, null, null);
    }

    private void saldoClientePorPeriodo() {
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");
        LocalDate inicio = ConsoleUtil.lerData("Data inicial (dd/MM/aaaa): ");
        LocalDate fim = ConsoleUtil.lerData("Data final (dd/MM/aaaa): ");

        RelatorioSaldoClienteDTO relatorio = relatorioService.gerarRelatorioSaldoClientePorPeriodo(documento, inicio, fim);

        imprimirSaldo(relatorio, inicio, fim);
    }

    private void imprimirSaldo(RelatorioSaldoClienteDTO relatorio, LocalDate inicio, LocalDate fim) {
        System.out.println();
        System.out.println("=================================================");
        if (inicio != null) {
            System.out.println("Período: " + ConsoleUtil.data(inicio) + " a " + ConsoleUtil.data(fim));
        }
        System.out.println("Cliente: " + relatorio.getNome() + " - Cliente desde: " + ConsoleUtil.data(relatorio.getDataCadastro()));
        System.out.println("Endereço: " + endereco(relatorio));
        System.out.println("Movimentações de crédito: " + relatorio.getQtdCredito());
        System.out.println("Movimentações de débito: " + relatorio.getQtdDebito());
        System.out.println("Total de movimentações: " + relatorio.getTotalMovimentacoes());
        System.out.println("Valor pago pelas movimentações: " + ConsoleUtil.dinheiro(relatorio.getValorPagoMovimentacoes()));
        System.out.println("Saldo inicial: " + ConsoleUtil.dinheiro(relatorio.getSaldoInicial()));
        System.out.println("Saldo atual: " + ConsoleUtil.dinheiro(relatorio.getSaldoAtual()));
        System.out.println("=================================================");
    }

    private String endereco(RelatorioSaldoClienteDTO relatorio) {
        if (relatorio.getLogradouro() == null) {
            return "não cadastrado";
        }
        return relatorio.getLogradouro()
                + ", " + relatorio.getNumero()
                + (relatorio.getComplemento() == null ? "" : ", " + relatorio.getComplemento())
                + ", " + relatorio.getBairro()
                + ", " + relatorio.getCidade()
                + ", " + relatorio.getUf()
                + ", " + relatorio.getCep();
    }

    private void receitaXpto() {
        LocalDate inicio = ConsoleUtil.lerDataOpcional("Data inicial (ENTER = últimos 30 dias): ");
        LocalDate fim = ConsoleUtil.lerDataOpcional("Data final (ENTER = hoje): ");

        RelatorioReceitaXptoDTO relatorio = relatorioService.gerarRelatorioReceitaXpto(inicio, fim);

        System.out.println();
        System.out.println("=================================================");
        System.out.println("RECEITA DA EMPRESA (XPTO)");
        System.out.println("Período: " + ConsoleUtil.data(relatorio.getPeriodoInicio()) + " a " + ConsoleUtil.data(relatorio.getPeriodoFim()));
        System.out.println("-------------------------------------------------");

        if (relatorio.getClientes().isEmpty()) {
            System.out.println("Nenhuma movimentação no período.");
        }
        for (RelatorioReceitaClienteDTO linha : relatorio.getClientes()) {
            System.out.println("Cliente: " + linha.getNomeCliente()
                    + " - Quantidade de movimentações: " + linha.getQuantidadeMovimentacoes()
                    + " - Valor das movimentações: " + ConsoleUtil.dinheiro(linha.getValorReceita()));
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Total de receitas: " + ConsoleUtil.dinheiro(relatorio.getTotalReceitas()));
        System.out.println("=================================================");
    }

    private void todosClientes() {
        LocalDate dataReferencia = ConsoleUtil.lerDataOpcional("Data de referência (ENTER = hoje): ");
        if (dataReferencia == null) {
            dataReferencia = LocalDate.now();
        }

        List<RelatorioTodosClientesDTO> clientes = relatorioService.gerarRelatorioTodosClientes(dataReferencia);

        System.out.println();
        System.out.println("=================================================");
        System.out.println("SALDO DE TODOS OS CLIENTES");
        System.out.println("Saldo em " + ConsoleUtil.data(dataReferencia));
        System.out.println("-------------------------------------------------");

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        }
        for (RelatorioTodosClientesDTO cliente : clientes) {
            System.out.println("Cliente: " + cliente.getNomeCliente()
                    + " - Cliente desde: " + ConsoleUtil.data(cliente.getDataCadastro())
                    + " - Saldo em " + ConsoleUtil.data(dataReferencia) + ": " + ConsoleUtil.dinheiro(cliente.getSaldo()));
        }

        System.out.println("=================================================");
    }
}
