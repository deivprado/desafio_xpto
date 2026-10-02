package davi.prado.XPTO.console;

import davi.prado.XPTO.dto.conta.ContaCreateDTO;
import davi.prado.XPTO.dto.conta.ContaDeleteDto;
import davi.prado.XPTO.dto.conta.ContaResponseDTO;
import davi.prado.XPTO.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Menu de contas (criar, listar, inativar e ativar).
 * <p>
 * Obs.: a opção "alterar" não está aqui porque hoje
 * {@code ContaService.alterarConta} usa os mesmos campos como chave de busca
 * e como valor novo, então na prática não altera nada. Fica para a etapa 2.
 */
@Component
@RequiredArgsConstructor
public class ContaMenu {

    private final ContaService contaService;

    public void abrir() {
        int opcao;

        do {
            System.out.println("\n--- CONTAS ---");
            System.out.println(" 1 - Criar");
            System.out.println(" 2 - Listar");
            System.out.println(" 3 - Inativar");
            System.out.println(" 4 - Ativar");
            System.out.println(" 0 - Voltar");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> listar();
                    case 3 -> inativar();
                    case 4 -> ativar();
                    case 0 -> { }
                    default -> System.out.println("\nOpção inválida!");
                }
            } catch (RuntimeException e) {
                System.out.println("\n>>> Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void criar() {
        System.out.println("\n--- Nova conta ---");
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");
        String instituicao = ConsoleUtil.lerTexto("Instituição financeira: ");
        String agencia = ConsoleUtil.lerTexto("Agência: ");
        String numeroConta = ConsoleUtil.lerTexto("Número da conta: ");

        ContaResponseDTO conta = contaService.criarConta(ContaCreateDTO.builder()
                .clienteDocumento(documento)
                .instituicaoFinanceira(instituicao)
                .agencia(agencia)
                .numeroConta(numeroConta)
                .build());

        System.out.println("\nConta criada!");
        imprimir(conta);
    }

    private void listar() {
        List<ContaResponseDTO> contas = contaService.consultarConta();

        if (contas.isEmpty()) {
            System.out.println("\nNenhuma conta cadastrada.");
            return;
        }

        System.out.println("\n--- Contas cadastradas (" + contas.size() + ") ---");
        for (ContaResponseDTO conta : contas) {
            imprimir(conta);
        }
    }

    private void inativar() {
        ContaResponseDTO conta = contaService.deletarConta(lerConta());

        System.out.println("\nConta inativada!");
        imprimir(conta);
    }

    private void ativar() {
        ContaResponseDTO conta = contaService.ativarConta(lerConta());

        System.out.println("\nConta ativada!");
        imprimir(conta);
    }

    /** Lê a identificação da conta (instituição + agência + número). */
    private ContaDeleteDto lerConta() {
        System.out.println("Informe a conta:");
        return ContaDeleteDto.builder()
                .instituicaoFinanceira(ConsoleUtil.lerTexto("Instituição financeira: "))
                .agencia(ConsoleUtil.lerTexto("Agência: "))
                .numeroConta(ConsoleUtil.lerTexto("Número da conta: "))
                .build();
    }

    private void imprimir(ContaResponseDTO conta) {
        System.out.println(" " + conta.getInstituicaoFinanceira()
                + " | ag " + conta.getAgencia()
                + " | conta " + conta.getNumeroConta()
                + " | ativo=" + conta.getAtivo()
                + " | cliente " + conta.getClienteDocumento());
    }
}
