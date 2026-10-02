package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.movimentacao.MovimentacaoCreateDTO;
import davi.prado.XPTO.dto.movimentacao.MovimentacaoResponseDTO;
import davi.prado.XPTO.entity.ContaEntity;
import davi.prado.XPTO.entity.MovimentacaoEntity;
import davi.prado.XPTO.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ContaService contaService;

    public MovimentacaoResponseDTO criarMovimentacao(MovimentacaoCreateDTO movimentacaoCreateDTO) {
        ContaEntity conta = contaService.consultarContaUnique(movimentacaoCreateDTO.getContaInstituicao(), movimentacaoCreateDTO.getContaAgencia(), movimentacaoCreateDTO.getContaNumero());

        MovimentacaoEntity movimentacaoNova = movimentacaoRepository.save(MovimentacaoEntity.builder()
                .tipoMovimentacao(movimentacaoCreateDTO.getTipoMovimentacao())
                .valor(movimentacaoCreateDTO.getValor())
                .dataMovimentacao(LocalDateTime.now())
                .descricao(movimentacaoCreateDTO.getDescricao())
                .conta(conta)
                .build());

        return converterParaResponseDTO(movimentacaoNova);
    }

    private MovimentacaoResponseDTO converterParaResponseDTO(MovimentacaoEntity movimentacao) {
        return MovimentacaoResponseDTO.builder()
                .tipoMovimentacao(movimentacao.getTipoMovimentacao())
                .valor(movimentacao.getValor())
                .dataMovimentacao(movimentacao.getDataMovimentacao())
                .descricao(movimentacao.getDescricao())
                .contaInstituicao(movimentacao.getConta().getInstituicaoFinanceira())
                .contaNumero(movimentacao.getConta().getNumeroConta())
                .contaAgencia(movimentacao.getConta().getAgencia())
                .build();
    }
}
