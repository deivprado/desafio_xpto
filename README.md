# XPTO

Projeto desenvolvido como desafio técnico, utilizando **Java 25**, **Spring Boot** e **Oracle Database**.

A ideia é um sistema simples para gerenciar clientes, endereços, contas bancárias e movimentações financeiras — controlando o saldo de cada cliente e a receita que a empresa (XPTO) ganha por cima dessas movimentações.

A interface é pelo **console**, com menus. O desafio deixa a saída dos relatórios em aberto ("Report, txt, console da IDE..."), então optei pelo console por ser mais direto de demonstrar e testar.

## 🛠️ Tecnologias

- Java 25
- Spring Boot
- Spring Data JPA / Hibernate
- Oracle Database
- PL/SQL (package com procedures, chamado pelo Java)
- Lombok
- Maven
- JUnit

## 📌 O que foi desenvolvido

- Cadastro, consulta, alteração e exclusão lógica de **clientes**
- Clientes **PF e PJ** na mesma tabela
- Cadastro e manutenção de **endereços** (mais de um por cliente)
- Cadastro e manutenção de **contas bancárias**
- Registro de **movimentações** financeiras
- **Movimentação inicial** (depósito de abertura) junto com o cadastro do cliente
- Saldo calculado a partir das movimentações
- **4 relatórios** financeiros
- **Integração Java ↔ Oracle** através de um package PL/SQL

O cliente é identificado pelo **documento** (CPF/CNPJ). Toda movimentação é lançada em uma **conta**, e o saldo do cliente é a soma do que passou por todas as contas dele.

O menu principal é assim:

```
=========================================
 XPTO - CONTROLE FINANCEIRO
=========================================
 1 - Clientes
 2 - Endereços
 3 - Contas
 4 - Movimentações
 5 - Relatórios
 0 - Sair
```

### Os relatórios

| # | Relatório | O que mostra |
|---|---|---|
| 1 | Saldo do cliente | Créditos, débitos, total de movimentações, valor pago, saldo inicial e saldo atual — com o endereço do cliente |
| 2 | Saldo do cliente por período | O mesmo do item 1, filtrado por um intervalo de datas |
| 3 | Saldo de todos os clientes | Uma linha por cliente, com o saldo em uma data de referência |
| 4 | Receita da empresa (XPTO) por período | Quanto cada cliente gerou de receita, e o total do período |

Um exemplo da saída do relatório de saldo:

```
Cliente: MARIA SILVA - Cliente desde: 02/10/2026
Endereço: RUA DAS FLORES, 100, APTO 12, CENTRO, SAO PAULO, SP, 01001000
Movimentações de crédito: 4
Movimentações de débito: 2
Total de movimentações: 6
Valor pago pelas movimentações: R$ 6,00
Saldo inicial: R$ 0,00
Saldo atual: R$ 2.494,60
```

Quem faz as contas é o banco: os quatro relatórios são **procedures** do package `PKG_RELATORIO`, chamadas pelo Java via JDBC (`CallableStatement` com `SYS_REFCURSOR`). O Java só formata e imprime — o PL/SQL devolve dados, não texto.

## 🗃️ Modelagem

```
Cliente
├── Endereço
└── Conta
    └── Movimentação
```

Um cliente pode possuir vários endereços e várias contas. As movimentações ficam vinculadas às contas para manter o histórico financeiro.

Para clientes PF e PJ optei por utilizar uma única tabela `CLIENTE`, já que grande parte das informações são compartilhadas entre os dois tipos. O tipo do cliente é definido a partir do documento:

- 11 dígitos → PF
- 14 dígitos → PJ

Os campos que não são comuns aos dois ficam opcionais: `data_nascimento` (só PF) e `nome_fantasia` (só PJ). Na validação, PF exige a data de nascimento e não aceita nome fantasia, e PJ é o contrário.

## 💡 A regra da receita da XPTO

A cobrança funciona em **janelas de 30 dias**, contadas a partir da data de cadastro do cliente. Dentro de cada janela, o valor por movimentação cai conforme a quantidade:

| Movimentações na janela | Valor por movimentação |
|---|---|
| até 10 | R$ 1,00 |
| de 11 a 20 | R$ 0,75 |
| acima de 20 | R$ 0,50 |

O enunciado não deixa claro se o valor é progressivo (como imposto de renda) ou se a faixa atingida define o preço de **todas** as movimentações. Optei pela **faixa cheia**: 15 movimentações na janela = 15 × R$ 0,75 = R$ 11,25.

A decisão está em um ponto só (o `CASE` dentro da procedure), então trocar para progressiva é uma alteração pequena.

## 🧮 Decisões que tomei

- **Exclusão lógica**: nada é apagado de verdade. Cliente, endereço e conta usam a coluna `ativo` ('S'/'N') para preservar o histórico.
- **Dados que não podem sofrer alteração**: documento, tipo do cliente e data de cadastro são imutáveis — mudar isso quebraria o histórico das movimentações.
- **Conta com movimentação não pode ser alterada**, apenas desativada.
- **Movimentação não tem alteração nem exclusão**: ela faz parte do histórico financeiro.
- **A tarifa da XPTO não desconta o saldo do cliente** — ela é receita da empresa, não despesa do cliente.
- **Saldo inicial**: no relatório completo é R$ 0,00 (marco zero, antes de qualquer movimentação); no relatório por período é o saldo acumulado **antes** da data inicial.
- **Endereço principal**: quando o cliente tem mais de um endereço ativo, o relatório mostra o primeiro cadastrado (menor id).
- **DTOs separados por operação** (`ClienteCreateDTO`, `ClienteUpdateDTO`, `ClienteResponseDTO`), assim cada operação recebe e devolve somente as informações necessárias.

## 🚀 Carga inicial (simulação da integração)

O desafio pede para simular a integração com as instituições financeiras, e sugere fazer isso com uma carga de dados no método principal. É exatamente o que acontece: ao subir, o `XptoApplication` chama `carregarDados()`, que cadastra 3 clientes (2 PF e 1 PJ), seus endereços e um punhado de movimentações "enviadas" pelas instituições.

Essa carga roda **uma vez só** — se já existe cliente cadastrado, ela é ignorada. E roda inteira dentro de **uma transação**: se algo falhar no meio, nada fica gravado, para o banco não ficar em um estado pela metade.

## 🧱 Organização

```
src/main/java/davi/prado/XPTO
├── XptoApplication.java     (método principal: carga inicial + menu)
├── console/                 (menus do console)
├── dto/                     (entrada e saída de dados)
├── entity/                  (mapeamento das tabelas)
├── exception/               (exceções de negócio)
├── repository/              (persistência)
└── service/                 (regras de negócio)
```

As Entity representam as tabelas do banco, os Repository cuidam da persistência, os Service concentram as regras de negócio e os DTOs são utilizados para entrada e saída de dados.

Também foram criadas exceções específicas para situações de negócio, como cliente não encontrado ou documento já cadastrado.

## 🧩 Padrões e boas práticas

- **Arquitetura em camadas**: `entity` → `repository` → `service` → `console`. O menu não tem regra de negócio: ele lê a opção, chama o service e imprime o DTO. Se um dia entrar uma API REST, os services continuam iguais.
- **Repository**: Spring Data JPA para o CRUD das entities. Para as procedures (que devolvem cursor) usei `CallableStatement` direto no repositório, porque ali não existe entity envolvida.
- **DTO**: nenhuma entity sai do service. Cada operação tem seu DTO de entrada e de saída.
- **Builder** (Lombok) para montar entities e DTOs, deixando a criação legível.
- **Injeção de dependência pelo construtor** (`@RequiredArgsConstructor`), sem instanciar nada na mão.
- **Exceções de negócio específicas** em vez de `RuntimeException` genérica, e o menu trata o erro mostrando só a mensagem para o usuário (sem stacktrace na cara dele).
- **Transações** onde faz sentido: o cadastro do cliente (cliente + conta + depósito inicial) e a carga inicial rodam cada um em uma transação.
- **Exclusão lógica** em vez de `DELETE`, para não perder o histórico.
- **Agregação no banco**: os relatórios somam e contam no Oracle (PL/SQL) em vez de trazer todas as movimentações para a memória do Java.

## ▶️ Como executar

É necessário ter instalado:

- Java 25
- Oracle Database

Os scripts do banco estão na pasta `Script de banco/` e devem rodar **nesta ordem**:

1. `Tabela Cliente.txt`
2. `Tabela Conta.txt`
3. `Tabela Endereco.txt`
4. `Tabela de Movimentacao.txt`
5. `PKG_RELATORIO.txt` (package com as procedures dos relatórios)

O usuário e a senha do banco **não ficam no código**: eles são lidos de variáveis de ambiente.

| Variável | Valor padrão | Para que serve |
|---|---|---|
| `DB_URL` | `jdbc:oracle:thin:@//localhost:1522/XEPDB1` | URL de conexão com o banco |
| `DB_USERNAME` | — (obrigatório) | usuário do banco |
| `DB_PASSWORD` | — (obrigatório) | senha do banco |


E se você roda pela IDE, é só cadastrar essas duas variáveis na configuração de execução (no IntelliJ: *Run → Edit Configurations → Environment variables*).

A configuração está com `ddl-auto=validate`, ou seja, o Hibernate **não cria** as tabelas — ele só confere se elas estão de acordo com as entities. Por isso os scripts precisam rodar antes.

Sobre os testes: existe a classe `XptoApplicationTests`, com o teste de contexto do Spring. Como ela sobe a aplicação inteira, ela precisa do Oracle acessível para rodar.

## 🤖 Sobre o uso de IA

Vou ser transparente quanto a isso, porque acho que faz parte de mostrar como eu trabalho.

A **camada de console** — os menus, a leitura dos dados digitados e a formatação da saída — foi **100% desenvolvida com IA**. É a parte de "front" da aplicação, e eu optei por usar a IA nela para ganhar tempo.

Todo o restante foi escrito por mim: a modelagem do banco, as entities, os DTOs, os repositories, os services, as regras de negócio, as decisões, os scripts SQL e o package PL/SQL. Nesses pontos a IA foi usada apenas como apoio para consultas — tirar dúvidas, revisar decisões, entender erros e discutir alternativas.


## 👨‍💻 Autor

Davi Prado

Projeto desenvolvido como parte de um desafio técnico, com foco em Java, Spring Boot, Oracle e PL/SQL.

