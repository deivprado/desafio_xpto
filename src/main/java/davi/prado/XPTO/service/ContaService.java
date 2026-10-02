package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.conta.ContaCreateDTO;
import davi.prado.XPTO.dto.conta.ContaDeleteDto;
import davi.prado.XPTO.dto.conta.ContaResponseDTO;
import davi.prado.XPTO.dto.conta.ContaUpdateDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.entity.ContaEntity;
import davi.prado.XPTO.entity.MovimentacaoEntity;
import davi.prado.XPTO.exception.ClienteNaoEncontradoException;
import davi.prado.XPTO.exception.ContaNaoEncontradaException;
import davi.prado.XPTO.exception.MovimentacaoJaExisteException;
import davi.prado.XPTO.repository.ClienteRepository;
import davi.prado.XPTO.repository.ContaRepository;
import davi.prado.XPTO.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ContaResponseDTO criarConta(ContaCreateDTO contaCreateDTO) {
        String documentoLimpo = contaCreateDTO.getClienteDocumento().replaceAll("[^0-9]", "");

        ClienteEntity clienteExistente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este documento!"));

        String numeroLimpo = contaCreateDTO.getNumeroConta().replaceAll("[^0-9]", "");

        Optional<ContaEntity> conta = contaRepository.findByUnique(contaCreateDTO.getInstituicaoFinanceira(), contaCreateDTO.getAgencia(), numeroLimpo);

        if (conta.isPresent()) {
            throw new MovimentacaoJaExisteException("Erro: Já existe uma conta com essas informações!");
        }

        ContaEntity contaNova = contaRepository.save(ContaEntity.builder()
                                        .instituicaoFinanceira(contaCreateDTO.getInstituicaoFinanceira().toUpperCase())
                                        .agencia(contaCreateDTO.getAgencia())
                                        .numeroConta(contaCreateDTO.getNumeroConta())
                                        .ativo("S")
                                        .cliente(clienteExistente)
                                        .build());

        return converterParaResponseDTO(contaNova);
    }

    public ContaResponseDTO alterarConta(ContaUpdateDTO contaUpdateDTO) {
        ContaEntity contaExistente = consultarContaUnique(contaUpdateDTO.getInstituicaoFinanceira(), contaUpdateDTO.getAgencia(), contaUpdateDTO.getNumeroConta());

        List<MovimentacaoEntity> movimentacaoExistente = movimentacaoRepository.findByConta(contaExistente.getId());

        if (!movimentacaoExistente.isEmpty()) {
            throw new MovimentacaoJaExisteException("Erro: Não é permitido altera uma conta que já tem movimentação!");
        }

        if (contaUpdateDTO.getInstituicaoFinanceira() != null) {
            contaExistente.setInstituicaoFinanceira(contaUpdateDTO.getInstituicaoFinanceira().toUpperCase());
        }
        if (contaUpdateDTO.getAgencia() != null) {
            contaExistente.setAgencia(contaUpdateDTO.getAgencia());
        }
        if (contaUpdateDTO.getNumeroConta() != null) {
            contaExistente.setNumeroConta(contaUpdateDTO.getNumeroConta());
        }

        contaRepository.save(contaExistente);

        return converterParaResponseDTO(contaExistente);
    }

    public ContaEntity consultarContaUnique(String instituicaoFinanceira, String agencia, String numeroConta) {
        String numeroLimpo = numeroConta.replaceAll("[^0-9]", "");

        return contaRepository.findByUnique(instituicaoFinanceira, agencia, numeroLimpo)
                .orElseThrow(() -> new ContaNaoEncontradaException("Erro: Essa conta não existe para esse cliente!"));
    }

    public List<ContaResponseDTO> consultarConta() {
        List<ContaEntity> listaConta =  contaRepository.findAll();
        List<ContaResponseDTO> listaResponse = new ArrayList<>();

        for (ContaEntity conta : listaConta) {
            listaResponse.add(converterParaResponseDTO(conta));
        }

        return listaResponse;
    }

    public ContaResponseDTO deletarConta(ContaDeleteDto contaDeleteDto) {
        return alterarAtivacaoConta(contaDeleteDto.getInstituicaoFinanceira(), contaDeleteDto.getAgencia(), contaDeleteDto.getNumeroConta(), "N");
    }

    public ContaResponseDTO ativarConta(ContaDeleteDto contaDeleteDto) {
        return alterarAtivacaoConta(contaDeleteDto.getInstituicaoFinanceira(), contaDeleteDto.getAgencia(), contaDeleteDto.getNumeroConta(), "S");
    }

    private ContaResponseDTO alterarAtivacaoConta(String instituicaoFinanceira, String agencia, String numero, String ativo) {
        ContaEntity contaExistente = consultarContaUnique(instituicaoFinanceira, agencia, numero);

        contaExistente.setAtivo(ativo);

        contaRepository.save(contaExistente);

        return converterParaResponseDTO(contaExistente);
    }

    private ContaResponseDTO converterParaResponseDTO(ContaEntity conta) {
        return ContaResponseDTO.builder()
                .instituicaoFinanceira(conta.getInstituicaoFinanceira())
                .agencia(conta.getAgencia())
                .numeroConta(conta.getNumeroConta())
                .ativo(conta.getAtivo())
                .clienteDocumento(conta.getCliente().getDocumento())
                .build();
    }
}
