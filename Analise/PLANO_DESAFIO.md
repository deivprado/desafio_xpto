# Análise do DESAFIO_DEV + Plano de Execução

> Documento de apoio. Nada aqui altera o código do projeto — pasta `Analise/` é só leitura/consulta.
> Data: 02/10/2026

---

## 1. O que o desafio realmente pede

Lendo o `DESAFIO_DEV.docx.pdf`, o que é **obrigatório**:

| # | Requisito | Observação |
|---|---|---|
| R1 | Projeto **Java + Oracle**, versionado no GitHub | já temos |
| R2 | **CRUD de clientes** (PF e PJ) | já temos (via service) |
| R3 | Manutenção de clientes, **protegendo dados que não podem mudar** | parcial |
| R4 | **CRUD de endereços** | já temos |
| R5 | **CRUD de contas**; se houver movimentação, **não permitir alteração** — só exclusão lógica | parcial (alteração já bloqueada) |
| R6 | **Movimentação inicial** no cadastro do cliente (ponto de partida) | já temos |
| R7 | **Simulação de integração** ("instituição envia movimentação") — pode ser **carga de dados no `main`** | ❌ falta |
| R8 | **Cálculo da receita da XPTO** (faixas por qtd de movimentações em janelas de 30 dias) | ❌ falta |
| R9 | **Relatório de saldo do cliente X** | ❌ falta |
| R10 | **Relatório de saldo do cliente X por período** | ❌ falta |
| R11 | **Relatório de saldo de todos os clientes** | ❌ falta |
| R12 | **Relatório de receita da XPTO por período** | ❌ falta |
| R13 | Tratar exceções | parcial |
| R14 | **Pelo menos 1 objeto PL/SQL** (procedure/trigger/function) **chamado pelo Java** | ❌ falta |
| R15 | **Classe de testes** | ❌ só existe `contextLoads` |
| R16 | **README** com observações, boas práticas e padrões de projeto | ❌ só 3 linhas |
| R17 | Saída dos relatórios: **Report, txt ou console** (livre) | ✔️ libera o console |
| R18 | Mínimo: **1 CRUD + 1 relatório** | já passaria, mas dá pra entregar bem mais |

**Conclusão importante:** o desafio **não pede API REST**. Ele pede regra de negócio + relatórios + PL/SQL + testes. E libera explicitamente a saída em console e a carga de dados no `main`.

---

## 2. Resposta direta: menu no terminal é uma boa solução?

**Sim — para o prazo de hoje, é a escolha certa.** Motivos:

1. Está **explicitamente permitido** (R17) e a simulação de integração pode ser carga no `main` (R7).
2. É o caminho de **menor risco**: não precisa de Postman, Swagger, coleção, front, nem subir servidor.
3. A avaliação, pelo próprio enunciado, olha "**a sua forma de desenvolvimento e o conhecimento em Java e Oracle**". O menu mostra o fluxo completo de ponta a ponta (cadastro → conta → movimentação → relatório) de forma demonstrável em segundos.
4. Permite **demonstração guiada** (o avaliador roda, escolhe números, vê os relatórios).

**O que eu faria (recomendação):** menu de console **+ carga inicial no `main`**. Ou seja, as duas coisas:
- ao subir a aplicação, um `CommandLineRunner` simula as movimentações recebidas das instituições (R7) e
- em seguida abre o menu interativo para CRUD e relatórios.

**Melhor que REST hoje?** REST + Swagger seria mais "bonito", mas consome tempo e não altera o critério de avaliação. Se sobrar tempo, o caminho barato de "brilhar a mais" é: manter o console como adaptador fino sobre os services (design limpo, trocável por REST depois) e caprichar no README.

> Regra de ouro: **o console não deve conter regra de negócio.** Ele só lê a opção, chama o service e imprime o DTO. Assim o código continua com arquitetura em camadas (o que o R16 pede pra explicar).

---

## 3. Situação atual do projeto (o que já está pronto)

```
✔ Entities: Cliente / Conta / Endereco / Movimentacao
✔ Repositories: 4 interfaces
✔ DTOs (pacotes renomeados para minúsculo — boa!)
✔ Services: Cliente, Conta, Endereco, Movimentacao
✔ Exceções de negócio (6)
✔ Scripts de criação das 4 tabelas (Script de banco/*.txt)
✔ Correções já feitas: erro de compilação do ContaService, numeroLimpo,
  movimentação inicial agora CREDITO, ativo de ContaEntity nullable=false, imports limpos
```

### Faltando / a fazer

```
❌ Não existe ponto de entrada: a app sobe (Spring Boot) e não faz nada.
❌ Nenhum relatório, nenhum cálculo de receita.
❌ Nenhum objeto PL/SQL nem chamada a partir do Java.
❌ Testes de verdade (só contextLoads) e README vazio.
❌ Consultas de relatório no repository (somas/contagens por cliente e período).
❌ Tratamento amigável de exceções fora do service (hoje estoura stacktrace no console).
△ Pendências de robustez: NPE em PF sem nomeFantasia, complemento nulo no endereço, etc.
```

---

## 4. Arquitetura proposta (só apresentação nova; nada de mexer no domínio)

```
src/main/java/davi/prado/XPTO/
├── XptoApplication.java          -> mantém @SpringBootApplication
├── config/
│   └── ConsoleRunner.java        -> CommandLineRunner chama a CargaInicial + MenuPrincipal
├── console/                      -> CAMADA DE APRESENTAÇÃO (nova)
│   ├── MenuPrincipal.java        -> loop principal + try/catch de exceções
│   ├── ClienteConsole.java
│   ├── ContaConsole.java
│   ├── EnderecoConsole.java
│   ├── MovimentacaoConsole.java
│   ├── RelatorioConsole.java     -> imprime os 4 relatórios formatados
│   └── ConsoleIO.java            -> ler int/datas (dd/MM/yyyy)/valores (pt-BR), limpar tela
├── service/
│   └── RelatorioService.java     -> NOVO: regra dos relatórios + receita XPTO
├── repository/                   -> novas @Query de soma/contagem
└── integration/
    └── CargaInicial.java         -> NOVO: simula movimentações vindas das instituições
```

### Menu sugerido

```
=========================================
 XPTO - CONTROLE FINANCEIRO
=========================================
 1 - Clientes          (CRUD + ativar/inativar)
 2 - Endereços         (CRUD + ativar/inativar)
 3 - Contas            (CRUD + ativar/inativar)
 4 - Movimentações     (lançar / listar)
 5 - Relatórios        (saldo cliente / período / todos / receita XPTO)
 6 - Simular integração (carga de movimentações)
 0 - Sair
-----------------------------------------
 Opção:
```

### Detalhe importante de implementação
Um `CommandLineRunner` interativo atrapalha os testes (`@SpringBootTest`). Solução simples:

```java
@Component
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true")
public class ConsoleRunner implements CommandLineRunner { ... }
```

e no `application.properties`:
```properties
app.console.enabled=true
```
Assim a aplicação sobe normal e os testes não travam esperando digitação.

---

## 5. Os 4 relatórios — formatos exatos do enunciado

**Relatório 1 — Saldo do cliente (geral)**
```
Cliente: X - Cliente desde: DD/MM/YYYY
Endereço: Rua, n°, complemento, bairro, cidade, UF, CEP
Movimentações de crédito: 00
Movimentações de débito: 0
Total de movimentações: 00
Valor pago pelas movimentações: 00,00
Saldo inicial: 0.000,00
Saldo atual: 00.000,00
```

**Relatório 2 — Saldo do cliente por período**
Igual ao 1, precedido de `Período: DD/MM/YYYY a DD/MM/YYYY`, filtrando as movimentações do período.

**Relatório 3 — Saldo de todos os clientes**
```
Cliente: X - Cliente desde: DD/MM/YYYY - Saldo em DD/MM/YYYY: 0.000,00
Cliente: Y - Cliente desde: DD/MM/YYYY - Saldo em DD/MM/YYYY: 000,00
```

**Relatório 4 — Receita da XPTO por período**
```
Período: DD/MM/YYYY a DD/MM/YYYY
Cliente X - Quantidade de movimentações: 80  - Valor das movimentações: R$ 0.000,00
Cliente Y - Quantidade de movimentações: 120 - Valor das movimentações: R$ 00.000,00
Total de receitas: R$ 00.000,00
```

---

## 6. Ambiguidades do enunciado (decidir e documentar no README)

Isso é ouro no README: mostrar que você **identificou** a ambiguidade e tomou uma decisão fundamentada.

1. **Faixas de preço — progressiva ou por faixa cheia?** ✅ **DECIDIDO EM 02/10: FAIXA CHEIA (flat)**
   - *Progressiva (imposto de renda):* 10 primeiras a R$1,00 + 10 seguintes a R$0,75 + resto a R$0,50 → 80 mov. = `R$ 47,50`.
   - *Faixa cheia (escolhida):* a faixa atingida define o preço de **todas** as movimentações →
     80 mov. = `80 × 0,50 = R$ 40,00` · 20 mov. = `R$ 15,00` · 10 mov. = `R$ 10,00`.
   - Bordas: `qtd ≤ 10` → R$ 1,00 · `11..20` → R$ 0,75 · `> 20` → R$ 0,50.
   - Manter num só lugar (`calcularReceita(qtd)`), fácil de trocar e de testar.

2. **"a cada período de 30 dias, a partir da data de cadastro"**
   - *Interpretação adotada:* o tempo do cliente é dividido em janelas fixas de 30 dias contadas da `dataCadastro`: `[0–30)`, `[30–60)`, `[60–90)`...
     A tarifa é calculada **por janela** (cada janela tem seu próprio enquadramento) e somada para o período do relatório.
   - Alternativa: contar 30 dias a partir da **primeira movimentação**. Documentar a escolha.

3. **A tarifa desconta o saldo do cliente?**
   - Interpretação: **não**. A tarifa é a *receita da XPTO*; o saldo do cliente é `saldo inicial + créditos − débitos`. O campo "Valor pago pelas movimentações" é informativo. Documentar.

4. **Dados imutáveis do cliente (R3)**: `documento (CPF/CNPJ)`, `tipoCliente` e `dataCadastro` **nunca** mudam (histórico). Nome/e-mail/telefone/ativo/endereço podem.

5. **Exclusão**: tudo é **lógica** (`ativo = 'S'/'N'`), nunca `DELETE` físico.

6. **Saldo inicial**: a movimentação inicial de abertura (R6). Bom guardá-la com uma `descricao` fixa/identificável para saber qual é o marco zero.

---

## 7. PL/SQL — decisão tomada: **procedure por relatório**

Veja o detalhamento em **`Analise/DECISAO_PROCEDURES.md`**. Resumo:

**Uma procedure por relatório**, com data **opcional** (`p_inicio IN DATE DEFAULT NULL`, `p_fim IN DATE DEFAULT NULL`):
parâmetros **nulos = relatório cheio**; preenchidos = **por período**. Assim o mesmo objeto atende os itens R9 e R10 —
sem duplicar query. (Não criar uma procedure para cada cenário.)

Pontos de atenção que estão detalhados naquele arquivo:
- `Saldo inicial` muda de significado no relatório por período (é o saldo **antes** de `p_inicio`) → usar `p_inicio IS NOT NULL` na soma dos movimentos anteriores.
- Filtro de fim de período: `m.data_movimentacao < p_fim + 1` (porque a coluna é `TIMESTAMP`).
- Relatório de **1 linha** (saldo do cliente) → `OUT` escalares; relatórios de **N linhas** (todos os clientes / receita) → `SYS_REFCURSOR` via `SimpleJdbcCall`.
- **Não formatar** datas/moeda/endereço dentro do PL/SQL — isso é do Java.

Com a faixa **cheia** escolhida, o cálculo da receita (por janela de 30 dias) é um `CASE` de uma linha:

```sql
CASE
  WHEN v_qtd <= 10 THEN v_qtd * 1.00
  WHEN v_qtd <= 20 THEN v_qtd * 0.75
  ELSE                 v_qtd * 0.50
END
```

Se preferir o caminho da **function escalar** (mais simples de chamar do Java via `SELECT FN_... FROM DUAL`),
a versão com faixa cheia seria:

```sql
CREATE OR REPLACE FUNCTION FN_RECEITA_CLIENTE(
    p_documento IN VARCHAR2,
    p_inicio    IN DATE,
    p_fim       IN DATE
) RETURN NUMBER IS
    v_qtd NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_qtd
      FROM movimentacao m
      JOIN conta c    ON c.id = m.conta_id
      JOIN cliente cl ON cl.id = c.cliente_id
     WHERE cl.documento = p_documento
       AND (p_inicio IS NULL OR m.data_movimentacao >= p_inicio)
       AND (p_fim    IS NULL OR m.data_movimentacao <  p_fim + 1);

    RETURN CASE
             WHEN v_qtd <= 10 THEN v_qtd * 1.00
             WHEN v_qtd <= 20 THEN v_qtd * 0.75
             ELSE                 v_qtd * 0.50
           END;
END;
/
```

> O Java fatia o período do relatório em **janelas de 30 dias** contadas da `dataCadastro` e chama o PL/SQL por janela —
> mantém a regra no banco simples e o controle no Java. Guardar em `Script de banco/` e **rodar antes** de subir a app.

---

## 8. Testes (R15)

`@SpringBootTest` sozinho **exige o Oracle no ar** — e a function PL/SQL não roda em H2. Caminho recomendado:

- **Testes de unidade com Mockito** (não sobem contexto):
  - `ReceitaServiceTest` → testa as 3 faixas + bordas (10, 20, 21) e o fatiamento em janelas de 30 dias. **Esse é o teste que mais impressiona.**
  - `ClienteServiceTest` → PF/PJ, documento inválido, documento duplicado, montagem da movimentação inicial.
  - `ContaServiceTest` → bloqueio de alteração quando existe movimentação.
- Manter (de preferência ajustar/renomear) o `contextLoads`, ou marcá-lo com `@Disabled`/profile de teste para não quebrar sem banco.

---

## 9. Ordem sugerida para hoje (timebox)

| Ordem | Tarefa | Por quê |
|---|---|---|
| 1 | `RelatorioService` + queries de soma no repository | é o **coração** da entrega |
| 2 | Regra de receita (faixa cheia, janelas de 30 dias) + procedures PL/SQL | R8 + R14 juntos |
| 3 | `ConsoleIO` + `MenuPrincipal` + `RelatorioConsole` | R17 / demonstração |
| 4 | Consoles de Cliente/Conta/Endereço/Movimentação | R2–R6 |
| 5 | `CargaInicial` (simulação de integração) | R7 |
| 6 | Tratamento de exceções no menu (não estourar stacktrace) | R13 |
| 7 | Testes (receita + cliente) | R15 |
| 8 | README (observações, decisões, boas práticas, padrões) | R16 |
| 9 | `git commit` + push (GitHub) | R1 |

Se o tempo apertar, os itens 1, 2, 3, 8 e 9 já entregam "1 CRUD + 1 relatório" com folga e com o PL/SQL.

---

## 10. Atenções técnicas (rápidas)

- **Lazy loading no console:** não há Open Session in View. Como `@ManyToOne` é EAGER por padrão, ok — mas **sempre devolva DTO** dos services para não vazar entidade.
- **Datas/valores:** ler com `DateTimeFormatter.ofPattern("dd/MM/yyyy")` e `NumberFormat` pt-BR; imprimir dinheiro com `pt-BR`.
- **NPEs conhecidos** (vale corrigir antes de demonstrar): PF com `nomeFantasia` nulo; `complemento` nulo no endereço; `valorInicial` nulo.
- **`ddl-auto=validate`:** as tabelas precisam existir com os tipos exatos. Ordem: rodar os 4 scripts → rodar o script PL/SQL → subir a app.
- **DevTools:** pode causar restart e interromper o console; se incomodar, rode sem ele.
- **`dto` renomeado para minúsculo:** ✔️ feito — só confirme que todos os imports apontam para `dto.cliente`, `dto.conta`, etc.

---

## 11. Decisões tomadas e pendências

**Decidido:**
- ✅ **Receita = faixa cheia (flat)**, aplicada por janelas de 30 dias contadas da `dataCadastro`.
- ✅ **PL/SQL = uma procedure por relatório**, com data opcional (parâmetros nulos = relatório cheio).
- ✅ Relatórios impressos no **console**; simulação de integração via **carga no `main`**.
- ✅ Você conduz a implementação dos relatórios; eu apoio com desenho, revisão e correção de código.

**Ainda em aberto (para decidir na hora de codar):**
1. **Regra dos 30 dias:** janelas fixas a partir de `dataCadastro` (recomendada) ou a partir da 1ª movimentação?
2. **Saldo inicial do relatório cheio:** `0` (recomendado, coerente com "antes de qualquer movimentação") ou o valor da movimentação de abertura?
3. A **tarifa desconta** o saldo do cliente? (recomendado: **não** — é receita da XPTO, não despesa do cliente)
4. Relatório do cliente: `OUT` escalares (recomendado) ou `SYS_REFCURSOR`?
