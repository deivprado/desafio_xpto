package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.Cliente.ClienteResponseDTO;
import davi.prado.XPTO.dto.Endereco.EnderecoCreateDTO;
import davi.prado.XPTO.dto.Endereco.EnderecoResponseDTO;
import davi.prado.XPTO.dto.Endereco.EnderecoUpdateDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.entity.EnderecoEntity;
import davi.prado.XPTO.exception.ClienteNaoEncontradoException;
import davi.prado.XPTO.exception.EnderecoJaCadastradoException;
import davi.prado.XPTO.exception.EnderecoNaoEncontradoException;
import davi.prado.XPTO.repository.ClienteRepository;
import davi.prado.XPTO.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    public EnderecoResponseDTO criarEndereco(EnderecoCreateDTO enderecoCreateDTO) {
        ClienteEntity cliente = clienteService.consultarClientePorDocumento(enderecoCreateDTO.getClienteDocumento());

        String cepLimpo = enderecoCreateDTO.getCep().replaceAll("[^0-9]", "");

        Optional<EnderecoEntity> enderecoExistente = enderecoRepository.findByUnique(cepLimpo, enderecoCreateDTO.getNumero(), cliente.getId());

        if (enderecoExistente.isPresent()) {
            throw new EnderecoJaCadastradoException("Erro: endereco já cadastrado para esse cliente!");
        }

        EnderecoEntity enderecoNovo = enderecoRepository.save(EnderecoEntity.builder()
                                            .logradouro(enderecoCreateDTO.getLogradouro().toUpperCase())
                                            .numero(enderecoCreateDTO.getNumero())
                                            .complemento(enderecoCreateDTO.getComplemento().toUpperCase())
                                            .bairro(enderecoCreateDTO.getBairro().toUpperCase())
                                            .cidade(enderecoCreateDTO.getCidade().toUpperCase())
                                            .uf(enderecoCreateDTO.getUf().toUpperCase())
                                            .cep(cepLimpo)
                                            .ativo("S")
                                            .cliente(cliente)
                                            .build());

        return converterParaResponseDTO(enderecoNovo);
    }

    public EnderecoResponseDTO alterarEndereco(String documento, String cep, String numero, EnderecoUpdateDTO enderecoUpdateDTO) {
        EnderecoEntity enderecoExistente = consultarEnderecoUnique(documento, cep, numero);

        if (enderecoUpdateDTO.getLogradouro() != null) {
            enderecoExistente.setLogradouro(enderecoUpdateDTO.getLogradouro().toUpperCase());
        }
        if (enderecoUpdateDTO.getNumero() != null) {
            enderecoExistente.setNumero(enderecoUpdateDTO.getNumero());
        }
        if (enderecoUpdateDTO.getComplemento() != null) {
            enderecoExistente.setComplemento(enderecoUpdateDTO.getComplemento().toUpperCase());
        }
        if (enderecoUpdateDTO.getBairro() != null) {
            enderecoExistente.setBairro(enderecoUpdateDTO.getBairro().toUpperCase());
        }
        if (enderecoUpdateDTO.getCidade() != null) {
            enderecoExistente.setCidade(enderecoUpdateDTO.getCidade().toUpperCase());
        }
        if (enderecoUpdateDTO.getUf() != null) {
            enderecoExistente.setUf(enderecoUpdateDTO.getUf().toUpperCase());
        }
        if (enderecoUpdateDTO.getCep() != null) {
            enderecoExistente.setCep(enderecoUpdateDTO.getCep());
        }

        enderecoRepository.save(enderecoExistente);

        return converterParaResponseDTO(enderecoExistente);
    }

    public EnderecoResponseDTO ativarEndereco(String documento, String cep, String numero) {
        return alterarAtivacaoEndereco(documento, cep, numero, "S");
    }

    public EnderecoResponseDTO deletarEndereco(String documento, String cep, String numero) {
        return alterarAtivacaoEndereco(documento, cep, numero, "N");
    }

    public List<EnderecoResponseDTO> consultarEndereco() {
        List<EnderecoEntity> listaEndereco =  enderecoRepository.findAll();
        List<EnderecoResponseDTO> listaResponse = new ArrayList<>();

        for (EnderecoEntity endereco : listaEndereco) {
            listaResponse.add(converterParaResponseDTO(endereco));
        }

        return listaResponse;
    }

    public EnderecoEntity consultarEnderecoUnique(String documento, String cep, String numero) {
        ClienteEntity cliente = clienteService.consultarClientePorDocumento(documento);

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        return  enderecoRepository.findByUnique(cepLimpo, numero, cliente.getId())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Erro: Esse endereço não existe para esse cliente!"));
    }

    private EnderecoResponseDTO alterarAtivacaoEndereco(String documento, String cep, String numero, String ativo) {
        EnderecoEntity enderecoExistente = consultarEnderecoUnique(documento, cep, numero);

        enderecoExistente.setAtivo(ativo);

        enderecoRepository.save(enderecoExistente);

        return converterParaResponseDTO(enderecoExistente);
    }

    private EnderecoResponseDTO converterParaResponseDTO(EnderecoEntity endereco) {
        return EnderecoResponseDTO.builder()
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .uf(endereco.getUf())
                .cep(endereco.getCep())
                .clienteDocumento(endereco.getCliente().getDocumento())
                .build();
    }
}
