package davi.prado.XPTO.console;

import davi.prado.XPTO.dto.cliente.ClienteCreateDTO;
import davi.prado.XPTO.dto.cliente.ClienteResponseDTO;
import davi.prado.XPTO.dto.cliente.ClienteUpdateDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Menu de clientes (cadastrar, listar, buscar, atualizar e inativar).
 */
@Component
@RequiredArgsConstructor
public class ClienteMenu {

    private final ClienteService clienteService;

    public void abrir() {
        int opcao;

        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println(" 1 - Cadastrar");
            System.out.println(" 2 - Listar");
            System.out.println(" 3 - Buscar por documento");
            System.out.println(" 4 - Atualizar");
            System.out.println(" 5 - Inativar");
            System.out.println(" 0 - Voltar");

            opcao = ConsoleUtil.lerInt(" Opção: ");

            try {
                switch (opcao) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> atualizar();
                    case 5 -> inativar();
                    case 0 -> { }
                    default -> System.out.println("\nOpção inválida!");
                }
            } catch (RuntimeException e) {
                System.out.println("\n>>> Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void cadastrar() {
        System.out.println("\n--- Novo cliente ---");
        String nome = ConsoleUtil.lerTexto("Nome: ");
        String documento = ConsoleUtil.lerTexto("CPF (11 dígitos) ou CNPJ (14 dígitos): ");
        String email = ConsoleUtil.lerTexto("E-mail: ");
        String telefone = ConsoleUtil.lerTexto("Telefone: ");
        LocalDate dataNascimento = ConsoleUtil.lerDataOpcional("Data de nascimento (só PF). ENTER se for PJ: ");
        String nomeFantasia = ConsoleUtil.lerTextoOpcional("Nome fantasia (só PJ). ENTER se for PF: ");

        System.out.println("--- Conta e depósito inicial ---");
        String instituicao = ConsoleUtil.lerTexto("Instituição financeira: ");
        String agencia = ConsoleUtil.lerTexto("Agência: ");
        String numeroConta = ConsoleUtil.lerTexto("Número da conta: ");
        BigDecimal valorInicial = ConsoleUtil.lerValor("Valor inicial: ");

        ClienteResponseDTO cliente = clienteService.criarCliente(ClienteCreateDTO.builder()
                .nome(nome)
                .documento(documento)
                .email(email)
                .telefone(telefone)
                .dataNascimento(dataNascimento)
                .nomeFantasia(nomeFantasia)
                .instituicaoFinanceira(instituicao)
                .agencia(agencia)
                .numeroConta(numeroConta)
                .valorInicial(valorInicial)
                .build());

        System.out.println("\nCliente cadastrado com sucesso!");
        imprimir(cliente);
    }

    private void listar() {
        List<ClienteResponseDTO> clientes = clienteService.consultarCliente();

        if (clientes.isEmpty()) {
            System.out.println("\nNenhum cliente cadastrado.");
            return;
        }

        System.out.println("\n--- Clientes cadastrados (" + clientes.size() + ") ---");
        for (ClienteResponseDTO cliente : clientes) {
            imprimir(cliente);
        }
    }

    private void buscar() {
        String documento = ConsoleUtil.lerTexto("Documento: ");

        ClienteEntity cliente = clienteService.consultarClientePorDocumento(documento);

        System.out.println();
        System.out.println("Nome: .......... " + cliente.getNome());
        System.out.println("Documento: ..... " + cliente.getDocumento());
        System.out.println("Tipo: .......... " + cliente.getTipoCliente());
        System.out.println("E-mail: ........ " + cliente.getEmail());
        System.out.println("Telefone: ...... " + cliente.getTelefone());
        System.out.println("Nascimento: .... " + ConsoleUtil.data(cliente.getDataNascimento()));
        System.out.println("Nome fantasia: . " + cliente.getNomeFantasia());
        System.out.println("Cliente desde: . " + ConsoleUtil.dataHora(cliente.getDataCadastro()));
        System.out.println("Ativo: ......... " + cliente.getAtivo());
    }

    private void atualizar() {
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");

        System.out.println("(ENTER nos campos que não quiser alterar)");
        ClienteUpdateDTO dto = ClienteUpdateDTO.builder()
                .nome(ConsoleUtil.lerTextoOpcional("Nome: "))
                .email(ConsoleUtil.lerTextoOpcional("E-mail: "))
                .telefone(ConsoleUtil.lerTextoOpcional("Telefone: "))
                .build();

        ClienteResponseDTO cliente = clienteService.atualizarCliente(documento, dto);

        System.out.println("\nCliente atualizado!");
        imprimir(cliente);
    }

    private void inativar() {
        String documento = ConsoleUtil.lerTexto("Documento do cliente: ");

        ClienteResponseDTO cliente = clienteService.deletarCliente(documento);

        System.out.println("\nCliente inativado!");
        imprimir(cliente);
    }

    private void imprimir(ClienteResponseDTO cliente) {
        System.out.println(" " + cliente.getDocumento()
                + " | " + cliente.getNome()
                + " | " + cliente.getTipoCliente()
                + " | ativo=" + cliente.getAtivo()
                + " | desde " + ConsoleUtil.dataHora(cliente.getDataCadastro()));
    }
}
