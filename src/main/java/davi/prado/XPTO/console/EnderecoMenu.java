package davi.prado.XPTO.console;

import davi.prado.XPTO.dto.endereco.EnderecoCreateDTO;
import davi.prado.XPTO.dto.endereco.EnderecoResponseDTO;
import davi.prado.XPTO.dto.endereco.EnderecoUpdateDTO;
import davi.prado.XPTO.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Menu de endereços (criar, listar, alterar, inativar e ativar).
 */
@Component
@RequiredArgsConstructor
public class EnderecoMenu {

    private final EnderecoService enderecoService;

    public void abrir() {
        int opcao;

        do {
            System.out.println("\n--- ENDEREÇOS ---");
            System.out.println(" 1 - Criar");
            System.out.println(" 2 - Listar");
            System.out.println(" 3 - Alterar");
            System.out.println(" 4 - Inativar");
            System.out.println(" 5 - Ativar");
            System.out.println(" 0 - Voltar");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            try {
                switch (opcao) {
                    case 1 -> criar();
                    case 2 -> listar();
                    case 3 -> alterar();
                    case 4 -> inativar();
                    case 5 -> ativar();
                    case 0 -> { }
                    default -> System.out.println("\nOpção inválida!");
                }
            } catch (RuntimeException e) {
                System.out.println("\n>>> Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void criar() {
        System.out.println("\n--- Novo endereço ---");
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");
        String logradouro = ConsoleUtil.lerTexto("Logradouro: ");
        String numero = ConsoleUtil.lerTexto("Número: ");
        String complemento = ConsoleUtil.lerTextoOpcional("Complemento (ENTER se não tiver): ");
        String bairro = ConsoleUtil.lerTexto("Bairro: ");
        String cidade = ConsoleUtil.lerTexto("Cidade: ");
        String uf = ConsoleUtil.lerTexto("UF: ");
        String cep = ConsoleUtil.lerTexto("CEP: ");

        EnderecoResponseDTO endereco = enderecoService.criarEndereco(EnderecoCreateDTO.builder()
                .clienteDocumento(documento)
                .logradouro(logradouro)
                .numero(numero)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .uf(uf)
                .cep(cep)
                .build());

        System.out.println("\nEndereço cadastrado!");
        imprimir(endereco);
    }

    private void listar() {
        List<EnderecoResponseDTO> enderecos = enderecoService.consultarEndereco();

        if (enderecos.isEmpty()) {
            System.out.println("\nNenhum endereço cadastrado.");
            return;
        }

        System.out.println("\n--- Endereços cadastrados (" + enderecos.size() + ") ---");
        for (EnderecoResponseDTO endereco : enderecos) {
            imprimir(endereco);
        }
    }

    private void alterar() {
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");
        String cep = ConsoleUtil.lerTexto("CEP do endereço: ");
        String numero = ConsoleUtil.lerTexto("Número do endereço: ");

        System.out.println("(ENTER nos campos que não quiser alterar)");
        EnderecoUpdateDTO dto = EnderecoUpdateDTO.builder()
                .logradouro(ConsoleUtil.lerTextoOpcional("Logradouro: "))
                .complemento(ConsoleUtil.lerTextoOpcional("Complemento: "))
                .bairro(ConsoleUtil.lerTextoOpcional("Bairro: "))
                .cidade(ConsoleUtil.lerTextoOpcional("Cidade: "))
                .uf(ConsoleUtil.lerTextoOpcional("UF: "))
                .build();

        EnderecoResponseDTO endereco = enderecoService.alterarEndereco(documento, cep, numero, dto);

        System.out.println("\nEndereço alterado!");
        imprimir(endereco);
    }

    private void inativar() {
        EnderecoResponseDTO endereco = enderecoService.deletarEndereco(
                ConsoleUtil.lerTexto("Documento do cliente: "),
                ConsoleUtil.lerTexto("CEP do endereço: "),
                ConsoleUtil.lerTexto("Número do endereço: "));

        System.out.println("\nEndereço inativado!");
        imprimir(endereco);
    }

    private void ativar() {
        EnderecoResponseDTO endereco = enderecoService.ativarEndereco(
                ConsoleUtil.lerTexto("Documento do cliente: "),
                ConsoleUtil.lerTexto("CEP do endereço: "),
                ConsoleUtil.lerTexto("Número do endereço: "));

        System.out.println("\nEndereço ativado!");
        imprimir(endereco);
    }

    private void imprimir(EnderecoResponseDTO endereco) {
        System.out.println(" " + endereco.getLogradouro()
                + ", " + endereco.getNumero()
                + (endereco.getComplemento() == null ? "" : ", " + endereco.getComplemento())
                + " - " + endereco.getBairro()
                + " - " + endereco.getCidade()
                + "/" + endereco.getUf()
                + " - CEP " + endereco.getCep()
                + " | ativo=" + endereco.getAtivo()
                + " | cliente " + endereco.getClienteDocumento());
    }
}
