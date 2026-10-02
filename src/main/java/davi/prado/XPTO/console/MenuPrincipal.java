package davi.prado.XPTO.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Menu principal: mostra as opções e chama o menu escolhido.
 */
@Component
@RequiredArgsConstructor
public class MenuPrincipal {

    private final ClienteMenu clienteMenu;
    private final EnderecoMenu enderecoMenu;
    private final ContaMenu contaMenu;
    private final MovimentacaoMenu movimentacaoMenu;
    private final RelatorioMenu relatorioMenu;

    public void abrir() {
        int opcao;

        do {
            System.out.println();
            System.out.println("=========================================");
            System.out.println(" XPTO - CONTROLE FINANCEIRO");
            System.out.println("=========================================");
            System.out.println(" 1 - Clientes");
            System.out.println(" 2 - Endereços");
            System.out.println(" 3 - Contas");
            System.out.println(" 4 - Movimentações");
            System.out.println(" 5 - Relatórios");
            System.out.println(" 0 - Sair");
            System.out.println("=========================================");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            switch (opcao) {
                case 1 -> clienteMenu.abrir();
                case 2 -> enderecoMenu.abrir();
                case 3 -> contaMenu.abrir();
                case 4 -> movimentacaoMenu.abrir();
                case 5 -> relatorioMenu.abrir();
                case 0 -> System.out.println("\nSaindo...");
                default -> System.out.println("\nOpção inválida!");
            }
        } while (opcao != 0);
    }
}
