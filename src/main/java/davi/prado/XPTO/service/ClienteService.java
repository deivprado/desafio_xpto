package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.Cliente.ClienteCreateDTO;
import davi.prado.XPTO.dto.Cliente.ClienteResponseDTO;
import davi.prado.XPTO.dto.Cliente.ClienteUpdateDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.repository.ClienteRepository;
import davi.prado.XPTO.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO criarCliente(ClienteCreateDTO clienteCreateDTO) {
        String documentoLimpo = clienteCreateDTO.getDocumento().replaceAll("[^0-9]", "");

        Optional<ClienteEntity> cliente = clienteRepository.findByDocumento(documentoLimpo);

        if (cliente.isPresent()) {
            throw new RuntimeException("Erro: Já existe um cliente cadastrado com este documento!");
        }

        String tipoCliente = buscarTipoCliente(documentoLimpo);

        validaCamposEspecificos(tipoCliente, clienteCreateDTO.getDataNascimento(), clienteCreateDTO.getNomeFantasia());

        ClienteEntity clienteNovo = clienteRepository.save(ClienteEntity.builder()
                                        .nome(clienteCreateDTO.getNome())
                                        .documento(documentoLimpo)
                                        .telefone(clienteCreateDTO.getTelefone())
                                        .email(clienteCreateDTO.getEmail())
                                        .tipoCliente(tipoCliente)
                                        .dataCadastro(LocalDate.now())
                                        .ativo("S")
                                        .dataNascimento(clienteCreateDTO.getDataNascimento())
                                        .nomeFantasia(clienteCreateDTO.getNomeFantasia())
                                        .build());


        //Ainda precisa fazer a regra de criar a conta e a movimentação

        return converterParaResponseDTO(clienteNovo);
    }

    private ClienteResponseDTO atualizarCliente(String documento, ClienteUpdateDTO clienteUpdateDTO) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new RuntimeException("Erro: Não existe um cliente cadastrado com este documento!"));

        if (clienteUpdateDTO.getNome() != null) {
            clienteExistente.setNome(clienteUpdateDTO.getNome());
        }
        if (clienteUpdateDTO.getTelefone() != null) {
            clienteExistente.setTelefone(clienteUpdateDTO.getTelefone());
        }
        if (clienteUpdateDTO.getEmail() != null) {
            clienteExistente.setEmail(clienteUpdateDTO.getEmail());
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
            nomeFantasiaFinal = clienteUpdateDTO.getNomeFantasia();
        } else {
            nomeFantasiaFinal = clienteExistente.getNomeFantasia();
        }

        validaCamposEspecificos(clienteExistente.getTipoCliente(), dataNascimentoFinal, nomeFantasiaFinal);

        clienteExistente.setDataNascimento(dataNascimentoFinal);
        clienteExistente.setNomeFantasia(nomeFantasiaFinal);

        clienteRepository.save(clienteExistente);

        return converterParaResponseDTO(clienteExistente);
    }

    private ClienteResponseDTO deletarCliente(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new RuntimeException("Erro: Não existe um cliente cadastrado com este documento!"));

        clienteExistente.setAtivo("N");

        clienteRepository.save(clienteExistente);

        return converterParaResponseDTO(clienteExistente);
    }

    private List<ClienteResponseDTO> consultarClientes() {
        List<ClienteEntity> listaCliente = clienteRepository.findAllByAtivo();
        List<ClienteResponseDTO> listaResponse = new ArrayList<>();

        for (ClienteEntity cliente : listaCliente) {
            listaResponse.add(converterParaResponseDTO(cliente));
        }

        return listaResponse;
    }

    private ClienteResponseDTO consultarClientePorDocumento(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new RuntimeException("Erro: Não existe um cliente cadastrado com este documento!"));

        return converterParaResponseDTO(clienteExistente);

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
