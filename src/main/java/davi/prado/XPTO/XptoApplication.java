package davi.prado.XPTO;

import davi.prado.XPTO.console.MenuPrincipal;
import davi.prado.XPTO.dto.cliente.ClienteCreateDTO;
import davi.prado.XPTO.dto.endereco.EnderecoCreateDTO;
import davi.prado.XPTO.dto.movimentacao.MovimentacaoCreateDTO;
import davi.prado.XPTO.service.ClienteService;
import davi.prado.XPTO.service.EnderecoService;
import davi.prado.XPTO.service.MovimentacaoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class XptoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext contexto = SpringApplication.run(XptoApplication.class, args);

		// Carga de dados inicial (simulação da integração com as instituições financeiras)
		carregarDados(contexto);

		// Menu no console
		contexto.getBean(MenuPrincipal.class).abrir();

		contexto.close();
	}

	/**
	 * Carga de dados inicial, feita junto do método principal.
	 * Roda só uma vez: se já existir cliente cadastrado, não faz nada.
	 */
	private static void carregarDados(ConfigurableApplicationContext contexto) {
		ClienteService clienteService = contexto.getBean(ClienteService.class);

		if (!clienteService.consultarCliente().isEmpty()) {
			System.out.println(">>> Carga inicial ignorada: já existem clientes cadastrados.");
			return;
		}

		System.out.println(">>> Carregando dados iniciais...");

		try {
			// Tudo em UMA transação: se algo falhar no meio, nada fica gravado.
			// (sem isso, uma falha no meio deixava o banco pela metade e a carga nunca mais completava)
			TransactionTemplate transacao = new TransactionTemplate(contexto.getBean(PlatformTransactionManager.class));

			transacao.execute(status -> {
				inserirDados(contexto);
				return null;
			});

			System.out.println(">>> Carga inicial concluída.");
		} catch (RuntimeException e) {
			System.out.println(">>> Falha na carga inicial (nada foi gravado): " + e.getMessage());
		}
	}

	/**
	 * Insere os dados da carga inicial: clientes (que já criam conta e depósito),
	 * endereços e as movimentações simulando a integração com as instituições.
	 */
	private static void inserirDados(ConfigurableApplicationContext contexto) {
		ClienteService clienteService = contexto.getBean(ClienteService.class);
		EnderecoService enderecoService = contexto.getBean(EnderecoService.class);

		// 1) Clientes. O cadastro do cliente já cria a conta e o depósito inicial.
		clienteService.criarCliente(ClienteCreateDTO.builder()
				.nome("Maria Silva")
				.documento("12345678901")                  // 11 dígitos -> PF
				.email("maria.silva@email.com")
				.telefone("(11) 98888-1111")
				.dataNascimento(LocalDate.of(1990, 5, 12))
				.instituicaoFinanceira("Banco do Brasil")
				.agencia("1234")
				.numeroConta("1001")
				.valorInicial(new BigDecimal("1500.00"))
				.build());

		clienteService.criarCliente(ClienteCreateDTO.builder()
				.nome("Joao Souza")
				.documento("98765432100")                  // 11 dígitos -> PF
				.email("joao.souza@email.com")
				.telefone("(21) 97777-2222")
				.dataNascimento(LocalDate.of(1985, 3, 20))
				.instituicaoFinanceira("Itau")
				.agencia("5678")
				.numeroConta("2002")
				.valorInicial(new BigDecimal("800.00"))
				.build());

		clienteService.criarCliente(ClienteCreateDTO.builder()
				.nome("XPTO Comercio LTDA")
				.documento("12345678000199")               // 14 dígitos -> PJ
				.email("contato@xptocomercio.com")
				.telefone("(11) 3333-4444")
				.nomeFantasia("XPTO Comercio")
				.instituicaoFinanceira("Bradesco")
				.agencia("9012")
				.numeroConta("3003")
				.valorInicial(new BigDecimal("5000.00"))
				.build());

		// 2) Endereços
		enderecoService.criarEndereco(EnderecoCreateDTO.builder()
				.clienteDocumento("12345678901")
				.logradouro("Rua das Flores")
				.numero("100")
				.complemento("Apto 12")
				.bairro("Centro")
				.cidade("Sao Paulo")
				.uf("SP")
				.cep("01001000")
				.build());

		enderecoService.criarEndereco(EnderecoCreateDTO.builder()
				.clienteDocumento("98765432100")
				.logradouro("Avenida Atlantica")
				.numero("250")
				.bairro("Copacabana")
				.cidade("Rio de Janeiro")
				.uf("RJ")
				.cep("22010010")
				.build());

		enderecoService.criarEndereco(EnderecoCreateDTO.builder()
				.clienteDocumento("12345678000199")
				.logradouro("Rodovia Anhanguera")
				.numero("1000")
				.complemento("Galpao 3")
				.bairro("Distrito Industrial")
				.cidade("Campinas")
				.uf("SP")
				.cep("13052000")
				.build());

		// 3) Movimentações (a "integração" enviando movimentações para as contas)
		movimentar(contexto, "CREDITO", "250.00", "TED recebida", "Banco do Brasil", "1234", "1001");
		movimentar(contexto, "DEBITO", "89.90", "Pagamento de boleto", "Banco do Brasil", "1234", "1001");
		movimentar(contexto, "DEBITO", "45.50", "Compra no debito", "Banco do Brasil", "1234", "1001");
		movimentar(contexto, "CREDITO", "1200.00", "PIX recebido", "Banco do Brasil", "1234", "1001");
		movimentar(contexto, "DEBITO", "320.00", "Pagamento de fornecedor", "Banco do Brasil", "1234", "1001");
		movimentar(contexto, "CREDITO", "99.99", "PIX recebido", "Itau", "5678", "2002");
		movimentar(contexto, "DEBITO", "150.75", "Conta de luz", "Itau", "5678", "2002");
		movimentar(contexto, "CREDITO", "3000.00", "TED recebida", "Bradesco", "9012", "3003");
		movimentar(contexto, "DEBITO", "780.40", "Folha de pagamento", "Bradesco", "9012", "3003");
		movimentar(contexto, "DEBITO", "120.00", "Frete", "Bradesco", "9012", "3003");
		movimentar(contexto, "CREDITO", "4500.00", "Venda a prazo", "Bradesco", "9012", "3003");
	}

	/** Registra uma movimentação como se ela tivesse vindo da instituição financeira. */
	private static void movimentar(ConfigurableApplicationContext contexto, String tipo, String valor,
	                               String descricao, String instituicao, String agencia, String numeroConta) {

		MovimentacaoService movimentacaoService = contexto.getBean(MovimentacaoService.class);

		movimentacaoService.criarMovimentacao(MovimentacaoCreateDTO.builder()
				.tipoMovimentacao(tipo)
				.valor(new BigDecimal(valor))
				.descricao(descricao)
				.contaInstituicao(instituicao)
				.contaAgencia(agencia)
				.contaNumero(numeroConta)
				.build());
	}

}
