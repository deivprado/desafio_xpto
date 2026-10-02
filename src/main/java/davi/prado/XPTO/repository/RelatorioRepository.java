package davi.prado.XPTO.repository;

import davi.prado.XPTO.dto.relatorio.RelatorioReceitaClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioSaldoClienteDTO;
import davi.prado.XPTO.dto.relatorio.RelatorioTodosClientesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RelatorioRepository {

    private final DataSource dataSource;

    public List<RelatorioSaldoClienteDTO> consultarSaldoCliente(Long clienteId) {
        List<RelatorioSaldoClienteDTO> lista = new ArrayList<>();
        String sql = "{call PKG_RELATORIO.PRC_REL_SALDO_CLIENTE(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setLong(1, clienteId);
            stmt.registerOutParameter(2, Types.REF_CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    lista.add(RelatorioSaldoClienteDTO.builder()
                            .nome(rs.getString("NOME"))
                            .dataCadastro(rs.getTimestamp("DATA_CADASTRO") != null ? rs.getTimestamp("DATA_CADASTRO").toLocalDateTime() : null)
                            .logradouro(rs.getString("LOGRADOURO"))
                            .numero(rs.getString("NUMERO"))
                            .complemento(rs.getString("COMPLEMENTO"))
                            .bairro(rs.getString("BAIRRO"))
                            .cidade(rs.getString("CIDADE"))
                            .uf(rs.getString("UF"))
                            .cep(rs.getString("CEP"))
                            .qtdCredito(rs.getLong("QTD_CREDITO"))
                            .qtdDebito(rs.getLong("QTD_DEBITO"))
                            .totalMovimentacoes(rs.getLong("TOTAL_MOVIMENTACOES"))
                            .valorPagoMovimentacoes(rs.getBigDecimal("VALOR_PAGO_MOVIMENTACOES"))
                            .saldoInicial(rs.getBigDecimal("SALDO_INICIAL"))
                            .saldoAtual(rs.getBigDecimal("SALDO_ATUAL"))
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar saldo do cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<RelatorioSaldoClienteDTO> consultarSaldoClientePorPeriodo(Long clienteId, LocalDate inicio, LocalDate fim) {
        List<RelatorioSaldoClienteDTO> lista = new ArrayList<>();
        String sql = "{call PKG_RELATORIO.PRC_REL_SALDO_CLIENTE_PERIODO(?, ?, ?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setLong(1, clienteId);
            stmt.setDate(2, Date.valueOf(inicio));
            stmt.setDate(3, Date.valueOf(fim));
            stmt.registerOutParameter(4, Types.REF_CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(4)) {
                while (rs.next()) {
                    lista.add(RelatorioSaldoClienteDTO.builder()
                            .nome(rs.getString("NOME"))
                            .dataCadastro(rs.getTimestamp("DATA_CADASTRO") != null ? rs.getTimestamp("DATA_CADASTRO").toLocalDateTime() : null)
                            .logradouro(rs.getString("LOGRADOURO"))
                            .numero(rs.getString("NUMERO"))
                            .complemento(rs.getString("COMPLEMENTO"))
                            .bairro(rs.getString("BAIRRO"))
                            .cidade(rs.getString("CIDADE"))
                            .uf(rs.getString("UF"))
                            .cep(rs.getString("CEP"))
                            .qtdCredito(rs.getLong("QTD_CREDITO"))
                            .qtdDebito(rs.getLong("QTD_DEBITO"))
                            .totalMovimentacoes(rs.getLong("TOTAL_MOVIMENTACOES"))
                            .valorPagoMovimentacoes(rs.getBigDecimal("VALOR_PAGO_MOVIMENTACOES"))
                            .saldoInicial(rs.getBigDecimal("SALDO_INICIAL"))
                            .saldoAtual(rs.getBigDecimal("SALDO_ATUAL"))
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar saldo por periodo: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<RelatorioReceitaClienteDTO> consultarReceitaXpto(LocalDate inicio, LocalDate fim) {
        List<RelatorioReceitaClienteDTO> lista = new ArrayList<>();
        String sql = "{call PKG_RELATORIO.PRC_REL_RECEITA_XPTO(?, ?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));
            stmt.registerOutParameter(3, Types.REF_CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
                while (rs.next()) {
                    lista.add(RelatorioReceitaClienteDTO.builder()
                            .nomeCliente(rs.getString("NOME_CLIENTE"))
                            .quantidadeMovimentacoes(rs.getLong("QUANTIDADE_MOVIMENTACOES"))
                            .valorReceita(rs.getBigDecimal("VALOR_RECEITA"))
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar receita XPTO: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<RelatorioTodosClientesDTO> consultarTodosClientes(LocalDate dataReferencia) {
        List<RelatorioTodosClientesDTO> lista = new ArrayList<>();
        String sql = "{call PKG_RELATORIO.PRC_REL_TODOS_CLIENTES(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, Date.valueOf(dataReferencia));
            stmt.registerOutParameter(2, Types.REF_CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    lista.add(RelatorioTodosClientesDTO.builder()
                            .nomeCliente(rs.getString("NOME"))
                            .dataCadastro(rs.getTimestamp("DATA_CADASTRO") != null ? rs.getTimestamp("DATA_CADASTRO").toLocalDateTime() : null)
                            .saldo(rs.getBigDecimal("SALDO"))
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar saldo de todos os clientes: " + e.getMessage(), e);
        }
        return lista;
    }
}