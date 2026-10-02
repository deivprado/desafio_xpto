package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.Cliente.ClienteCreateDTO;
import davi.prado.XPTO.dto.Cliente.ClienteResponseDTO;
import davi.prado.XPTO.dto.Cliente.ClienteUpdateDTO;
import davi.prado.XPTO.dto.Conta.ContaCreateDTO;
import davi.prado.XPTO.dto.Movimentacao.MovimentacaoCreateDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.exception.ClienteJaCadastradoException;
import davi.prado.XPTO.exception.ClienteNaoEncontradoException;
import davi.prado.XPTO.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaService contaService;
    private final MovimentacaoService movimentacaoService;

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteCreateDTO clienteCreateDTO) {
        String documentoLimpo = clienteCreateDTO.getDocumento().replaceAll("[^0-9]", "");
        String numeroLimpo = clienteCreateDTO.getTelefone().replaceAll("[^0-9]", "");

        Optional<ClienteEntity> cliente = clienteRepository.findByDocumento(documentoLimpo);

        if (cliente.isPresent()) {
            throw new ClienteJaCadastradoException("Erro: Já existe um cliente cadastrado com este documento!");
        }

        String tipoCliente = buscarTipoCliente(documentoLimpo);

        validaCamposEspecificos(tipoCliente, clienteCreateDTO.getDataNascimento(), clienteCreateDTO.getNomeFantasia());

        ClienteEntity clienteNovo = clienteRepository.save(ClienteEntity.builder()
                                        .nome(clienteCreateDTO.getNome().toUpperCase())
                                        .documento(documentoLimpo)
                                        .telefone(numeroLimpo)
                                        .email(clienteCreateDTO.getEmail().toUpperCase())
                                        .tipoCliente(tipoCliente)
                                        .dataCadastro(LocalDateTime.now())
                                        .ativo("S")
                                        .dataNascimento(clienteCreateDTO.getDataNascimento())
                                        .nomeFantasia(clienteCreateDTO.getNomeFantasia().toUpperCase())
                                        .build());

        ContaCreateDTO contaNova = ContaCreateDTO.builder()
                .instituicaoFinanceira(clienteCreateDTO.getInstituicaoFinanceira())
                .agencia(clienteCreateDTO.getAgencia())
                .numeroConta(clienteCreateDTO.getNumeroConta())
                .clienteDocumento(clienteNovo.getDocumento())
                .build();

        contaService.criarConta(contaNova);

        MovimentacaoCreateDTO movimentacaoNova = MovimentacaoCreateDTO.builder()
                .tipoMovimentacao("DEBITO")
                .valor(clienteCreateDTO.getValorInicial())
                .descricao("DEPÓSITO INICIAL DE ABERTURA DE CONTA")
                .contaInstituicao(clienteCreateDTO.getInstituicaoFinanceira())
                .contaAgencia(clienteCreateDTO.getAgencia())
                .contaNumero(clienteCreateDTO.getNumeroConta())
                .build();

        movimentacaoService.criarMovimentacao(movimentacaoNova);

        return converterParaResponseDTO(clienteNovo);
    }

    public ClienteResponseDTO atualizarCliente(String documento, ClienteUpdateDTO clienteUpdateDTO) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");
        String numeroLimpo = clienteUpdateDTO.getTelefone().replaceAll("[^0-9]", "");


        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este documento!"));

        if (clienteUpdateDTO.getNome() != null) {
            clienteExistente.setNome(clienteUpdateDTO.getNome().toUpperCase());
        }
        if (clienteUpdateDTO.getTelefone() != null) {
            clienteExistente.setTelefone(numeroLimpo);
        }
        if (clienteUpdateDTO.getEmail() != null) {
            clienteExistente.setEmail(clienteUpdateDTO.getEmail().toUpperCase());
        }
        if (clienteUpdateDTO.getAtivo() != null) {
            clienteExistente.setAtivo(clienteUpdateDTO.getAtivo());
        }

        LocalDate dataNascimentoFinal;
        if (clienteUpdateDTO.getDataNascimento() != null) {
            dataNascimentoFinal = clienteUpdateDTO.getDataNascimento();
        } else {
            dataNascimentoFinal = clienteExistente.getDataNascimento();
        }

        String nomeFantasiaFinal;
        if (clienteUpdateDTO.getNomeFantasia() != null) {
            nomeFantasiaFinal = clienteUpdateDTO.getNomeFantasia().toUpperCase();
        } else {
            nomeFantasiaFinal = clienteExistente.getNomeFantasia().toUpperCase();
        }

        validaCamposEspecificos(clienteExistente.getTipoCliente(), dataNascimentoFinal, nomeFantasiaFinal);

        clienteExistente.setDataNascimento(dataNascimentoFinal);
        clienteExistente.setNomeFantasia(nomeFantasiaFinal);

        clienteRepository.save(clienteExistente);

        return converterParaResponseDTO(clienteExistente);
    }

    public ClienteResponseDTO deletarCliente(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este documento!"));

        clienteExistente.setAtivo("N");

        clienteRepository.save(clienteExistente);

        return converterParaResponseDTO(clienteExistente);
    }

    public List<ClienteResponseDTO> consultarCliente() {
        List<ClienteEntity> listaCliente = clienteRepository.findAll();
        List<ClienteResponseDTO> listaResponse = new ArrayList<>();

        for (ClienteEntity cliente : listaCliente) {
            listaResponse.add(converterParaResponseDTO(cliente));
        }

        return listaResponse;
    }

    public ClienteEntity consultarClientePorDocumento(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este documento!"));

        return clienteExistente;
    }

    private String buscarTipoCliente(String documento){
        String tipoCliente = "";

        if (documento.length() == 11) {
            tipoCliente = "PF";
        } else if (documento.length() == 14) {
            tipoCliente = "PJ";
        } else {
            throw new RuntimeException("Erro: Documento inválido!");
        }

        return (tipoCliente);
    }

    private void validaCamposEspecificos(String tipoCliente, LocalDate dataNascimento, String nomeFantasia) {

        if (tipoCliente.equals("PF")) {
            if (dataNascimento == null) {
                throw new RuntimeException("Erro: Para cliente PF é necessário informar a data de nascimento!");
            } else if (nomeFantasia.isBlank()) {
                throw new RuntimeException("Erro: Para cliente PF não pode preencher o nome fantasia!");
            }
        } else {
            if (nomeFantasia.isBlank()) {
                throw new RuntimeException("Erro: Para cliente PJ é necessário informar o nome fantasia!");
            } else if (dataNascimento != null) {
                throw new RuntimeException("Erro: Para cliente PJ não pode preencher a data de nascimento!");
            }
        }
    }

    private ClienteResponseDTO converterParaResponseDTO(ClienteEntity cliente) {
        return ClienteResponseDTO.builder()
                .documento(cliente.getDocumento())
                .nome(cliente.getNome())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .tipoCliente(cliente.getTipoCliente())
                .dataCadastro(cliente.getDataCadastro())
                .ativo(cliente.getAtivo())
                .dataNascimento(cliente.getDataNascimento())
                .nomeFantasia(cliente.getNomeFantasia())
                .build();
    }
}
