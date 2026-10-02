# Decisão: procedures para os relatórios (uma por relatório × uma por cenário)

> Resposta à pergunta: *"com uma procedure conseguimos fazer o relatório do cliente cheio e por período
> (se mandar parâmetro faz por período, se mandar vazio faz cheio)? ou é melhor uma procedure para cada cálculo?"*
> Data: 02/10/2026 · Nada aqui altera o projeto.

---

## 1. Resposta curta

**Sim, dá certo** — e, na minha opinião, **é o caminho certo**: **uma procedure por RELATÓRIO**, com os parâmetros
de data **opcionais** (`DEFAULT NULL`), em vez de uma procedure para cada cenário.

- `p_inicio`/`p_fim` **preenchidos** → relatório **por período**.
- `p_inicio`/`p_fim` **nulos** → relatório **cheio** (todo o histórico).

Não crie uma procedure "cheia" e outra "por período" para o mesmo relatório: seria a mesma query duplicada em
dois lugares. Um `DEFAULT NULL` resolve os dois casos e você mantém **uma única fonte de verdade**.

---

## 2. Atenção nº 1 — "Saldo inicial" **muda de significado**

Esse é o ponto que faz a maioria errar:

| Relatório | `Saldo inicial` significa | `Saldo atual` significa |
|---|---|---|
| **Cheio** | saldo **antes da primeira** movimentação = `0` | saldo depois de tudo |
| **Por período** | saldo **acumulado antes** de `p_inicio` | saldo no **fim** de `p_fim` |

Se você simplesmente ignorar o filtro quando o parâmetro é nulo, o "saldo inicial" do relatório cheio vira a soma
de tudo e o "saldo atual" fica dobrado. A boa notícia: a fórmula **"saldo inicial = saldo de tudo que veio ANTES do
início do período"** funciona nos dois casos, porque com `p_inicio` nulo ela naturalmente vale `0`:

```sql
-- saldo inicial: SOMENTE movimentações anteriores ao período
SELECT NVL(SUM(CASE WHEN m.tipo_movimentacao = 'CREDITO'
                    THEN m.valor ELSE -m.valor END), 0)
  INTO v_saldo_inicial
  FROM movimentacao m
  JOIN conta c   ON c.id = m.conta_id
  JOIN cliente cl ON cl.id = c.cliente_id
 WHERE cl.documento = p_documento
   AND p_inicio IS NOT NULL              -- <- quando for o relatório cheio, não entra nenhuma linha
   AND m.data_movimentacao < p_inicio;
```

Repare no `p_inicio IS NOT NULL`: é ele que faz o relatório cheio devolver `0` em vez de somar tudo.
`Saldo atual = saldo inicial + créditos do período − débitos do período`.

---

## 3. Filtro de período — cuidado com o último dia

`data_movimentacao` é `TIMESTAMP`. Se você usar `BETWEEN p_inicio AND p_fim`, **as movimentações do último dia
inteiro ficam de fora** (porque `p_fim` é meia-noite). Use intervalo **semiaberto**:

```sql
AND (p_inicio IS NULL OR m.data_movimentacao >= p_inicio)
AND (p_fim    IS NULL OR m.data_movimentacao <  p_fim + 1)
```

Esse `+ 1` no Oracle soma **um dia** a uma `DATE`/`TIMESTAMP`, incluindo todo o último dia. É o padrão que eu usaria.

---

## 4. Granularidade recomendada — 3 procedures, não 6

| Procedure | Retorno | Serve para |
|---|---|---|
| `SP_REL_CLIENTE_SALDO(p_documento, p_inicio DEFAULT NULL, p_fim DEFAULT NULL, ...)` | 1 linha | Relatório 1 (cheio) **e** Relatório 2 (período) |
| `SP_REL_TODOS_CLIENTES(p_data_ref DEFAULT NULL, ...)` | N linhas | Relatório 3 |
| `SP_REL_RECEITA_XPTO(p_inicio DEFAULT NULL, p_fim DEFAULT NULL, ...)` | N linhas + total | Relatório 4 |

Ou seja: **a data opcional é uma característica de cada relatório**, não um motivo para criar uma procedure extra.

No Java eu deixaria a decisão o mais explícita possível (e o console simples):

```java
public RelatorioClienteDTO gerarRelatorioCliente(String documento) {              // cheio
    return gerarRelatorioCliente(documento, null, null);
}
public RelatorioClienteDTO gerarRelatorioCliente(String documento, LocalDate i, LocalDate f) {
    // mesma procedure; i/f nulos = todo o histórico
}
```

---

## 5. Como DEVOLVER os dados — aqui está a parte que dá dor de cabeça no Java

Procedure no Oracle **não retorna valor**; ela devolve por `OUT`. Duas formas:

### Opção A — parâmetros `OUT` escalares ✅ (recomendada para o relatório de **1 linha**)
```sql
PROCEDURE SP_REL_CLIENTE_SALDO(
    p_documento      IN  VARCHAR2,
    p_inicio         IN  DATE DEFAULT NULL,
    p_fim            IN  DATE DEFAULT NULL,
    o_qtd_credito    OUT NUMBER,
    o_qtd_debito     OUT NUMBER,
    o_total_mov      OUT NUMBER,
    o_valor_pago     OUT NUMBER,
    o_saldo_inicial  OUT NUMBER,
    o_saldo_atual    OUT NUMBER
)
```
No Java é leitura direta de `OUT` via `SimpleJdbcCall` (ou `StoredProcedureQuery`) — sem mapear cursor, sem
surpresa. Para um relatório de **uma linha só**, essa é a opção mais simples e robusta.

### Opção B — `SYS_REFCURSOR` OUT (necessária para relatórios de **N linhas**: itens 3 e 4)
```sql
PROCEDURE SP_REL_TODOS_CLIENTES(
    p_data_ref IN DATE DEFAULT NULL,
    o_cursor   OUT SYS_REFCURSOR
)
```
⚠️ Aviso honesto: mapear `SYS_REFCURSOR` no **Spring Data JPA (`@Procedure`)** é chato e cheio de detalhes
(dialeto, `hibernate.proc.param_null_passing`, tipo de retorno). O caminho que **funciona sem brigar** é
`SimpleJdbcCall` (já vem com o `spring-jdbc`, que está no classpath via `spring-boot-starter-data-jpa`):

```java
SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
        .withProcedureName("SP_REL_TODOS_CLIENTES")
        .declareParameters(
            new SqlParameter("p_data_ref", Types.DATE),
            new SqlOutParameter("o_cursor", OracleTypes.CURSOR, new ColumnMapRowMapper()));

Map<String, Object> out = call.execute(new MapSqlParameterSource().addValue("p_data_ref", dataRef));
@SuppressWarnings("unchecked")
List<Map<String, Object>> linhas = (List<Map<String, Object>>) out.get("o_cursor");
```
(`OracleTypes.CURSOR` = `oracle.jdbc.OracleTypes.CURSOR`; o driver `ojdbc17` já está no projeto.)

**Resumo da recomendação:** `OUT` escalares para o relatório do cliente (1 linha); `SYS_REFCURSOR` para
"todos os clientes" e "receita XPTO" (N linhas).

---

## 6. O que **não** colocar dentro do PL/SQL

O PL/SQL deve devolver **dados**, não o **texto do relatório**. Deixe fora da procedure:
- formatação de data (`dd/MM/yyyy`) e moeda (`R$ 0.000,00`) → **Java** (é apresentação, e evita dor com NLS do Oracle);
- concatenar o endereço (`Rua, nº, complemento, bairro, cidade, UF, CEP`) → **Java**;
- cabeçalhos, `RPAD`, traços, "Período: ..." → **Java**.

Isso mantém a procedure testável e reaproveitável, e ainda deixa o Java com papel claro: **repositório → procedure →
DTO → formatação → console**. (E, de brinde, é exatamente o tipo de decisão de arquitetura que o README do desafio
pede para você justificar.)

---

## 7. Sobre a sua decisão de hoje: faixa **cheia** (flat)

Com a faixa cheia, o cálculo da receita fica bem mais simples (uma linha de `CASE`):

```sql
-- qtd = quantidade de movimentações do cliente na janela de 30 dias
CASE
  WHEN v_qtd <= 10 THEN v_qtd * 1.00
  WHEN v_qtd <= 20 THEN v_qtd * 0.75
  ELSE                 v_qtd * 0.50
END
```

**Decisão de borda a documentar:** `qtd = 10` → R$ 1,00 (faixa "até 10"); `qtd = 11..20` → R$ 0,75; `qtd > 20` → R$ 0,50.
Exemplos: 10 mov. = **R$ 10,00**; 20 mov. = **R$ 15,00**; 80 mov. = **R$ 40,00**.

> Continua valendo a dúvida do **corte de 30 dias**: eu fatiava o período do relatório em janelas de 30 dias contadas da
> `dataCadastro` (`[0–30)`, `[30–60)`, ...), aplico o `CASE` em cada janela e somo. O Java controla o fatiamento e chama
> a procedure por janela — assim a regra PL/SQL continua simples.

---

## 8. Checklist de armadilhas (Oracle + Java)

- [ ] Parâmetro opcional é `IN DATE DEFAULT NULL` — e, no Java, o `null` produz o mesmo efeito. (Bônus: `p_fim` nulo com
      `p_inicio` preenchido = "a partir de", sem precisar de outra procedure.)
- [ ] Validar `p_inicio > p_fim` antes de chamar (senão o relatório volta vazio e parece bug).
- [ ] `TIMESTAMP` × `DATE`: usar `< p_fim + 1` (item 3).
- [ ] Lista de parâmetros do `SimpleJdbcCall` deve bater exatamente com a da procedure (nome e tipo).
- [ ] `GRANT EXECUTE` para o usuário `davi` (se a procedure ficar em outro schema).
- [ ] Guardar o script em `Script de banco/` e **rodar antes** de subir a aplicação (`ddl-auto=validate`).
- [ ] Não duplicar a mesma agregação em Java **e** no PL/SQL — escolha um lado por relatório.

---

## 9. Situação: script PL/SQL criado ✅

O script completo das 3 procedures está em **`Script de banco/PLSQL - Relatorios XPTO.txt`**
(com o `CASE` de faixa cheia, o `p_inicio IS NOT NULL` do saldo inicial e o `< p_fim + 1`).

**Decisão de retorno:** as 3 procedures devolvem **`SYS_REFCURSOR`** — inclusive a do cliente (1 linha).
Motivo: o relatório do cliente tem muitos campos (inclui o endereço), então um cursor mantém a assinatura estável e
deixa as 3 procedures uniformes — o Java usa **um único helper** (`SimpleJdbcCall` + `ColumnMapRowMapper`) para todas.
Se depois quiser, trocar a do cliente para `OUT` escalares é barato (é a única que muda).

Observação: as colunas do cursor chegam ao Java com o nome em **MAIÚSCULO** (padrão Oracle) —
ex.: `NOME`, `SALDO_ATUAL`, `VALOR_RECEITA`.

**Próximo passo natural:** o **esqueleto Java** de chamada (`SimpleJdbcCall` + DTOs + `RelatorioService`).
