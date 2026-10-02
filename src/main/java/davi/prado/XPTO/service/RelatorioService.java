package davi.prado.XPTO.service;

import davi.prado.XPTO.dto.relatorio.RelatorioReceitaClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioReceitaXptoDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioSaldoClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioTodosClientesDTO;
import davi.prado.XPTO.entity.ClienteEntity;
import davi.prado.XPTO.exception.ClienteNaoEncontradoException;
import davi.prado.XPTO.exception.PeriodoInvalidoException;
import davi.prado.XPTO.repository.ClienteRepository;
import davi.prado.XPTO.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelatorioService {

    private static final int DIAS_JANELA_PADRAO = 30;

    private final RelatorioRepository relatorioRepository;
    private final ClienteRepository clienteRepository;

    public RelatorioSaldoClienteDTO gerarRelatorioSaldoCliente(String documento) {
        Long clienteId = resolverClienteId(documento);

        List<RelatorioSaldoClienteDTO> linhas = relatorioRepository.consultarSaldoCliente(clienteId);

        return primeiraLinha(linhas);
    }

    public RelatorioSaldoClienteDTO gerarRelatorioSaldoClientePorPeriodo(String documento, LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);

        Long clienteId = resolverClienteId(documento);

        List<RelatorioSaldoClienteDTO> linhas = relatorioRepository.consultarSaldoClientePorPeriodo(clienteId, inicio, fim);

        return primeiraLinha(linhas);
    }

    public RelatorioReceitaXptoDTO gerarRelatorioReceitaXpto(LocalDate inicio, LocalDate fim) {
        LocalDate fimFinal = (fim != null) ? fim : LocalDate.now();
        LocalDate inicioFinal = (inicio != null) ? inicio : fimFinal.minusDays(DIAS_JANELA_PADRAO);

        validarPeriodo(inicioFinal, fimFinal);

        List<RelatorioReceitaClienteDTO> clientes = relatorioRepository.consultarReceitaXpto(inicioFinal, fimFinal);

        BigDecimal totalReceitas = clientes.stream()
                .map(RelatorioReceitaClienteDTO::getValorReceita)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RelatorioReceitaXptoDTO.builder()
                .periodoInicio(inicioFinal)
                .periodoFim(fimFinal)
                .clientes(clientes)
                .totalReceitas(totalReceitas)
                .build();
    }

    public List<RelatorioTodosClientesDTO> gerarRelatorioTodosClientes(LocalDate dataReferencia) {
        LocalDate dataRefFinal = (dataReferencia != null) ? dataReferencia : LocalDate.now();

        return relatorioRepository.consultarTodosClientes(dataRefFinal);
    }

    private Long resolverClienteId(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        ClienteEntity cliente = clienteRepository.findByDocumento(documentoLimpo)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este documento!"));

        return  cliente.getId();
    }

    private RelatorioSaldoClienteDTO primeiraLinha(List<RelatorioSaldoClienteDTO> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            throw new ClienteNaoEncontradoException("Erro: Não existe um cliente cadastrado com este ID!");
        }
        return linhas.get(0);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new PeriodoInvalidoException("Erro: Período inválido: informe a data inicial e a data final!");
        }
        if (inicio.isAfter(fim)) {
            throw new PeriodoInvalidoException("Erro: Período inválido: a data inicial não pode ser maior que a data final!");
        }
    }
}
