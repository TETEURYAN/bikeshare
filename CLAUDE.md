# Prompt para o Claude Code — Template Java + Testes de Aceitação do BIKESHARE

## 0. Como usar este arquivo

Este documento foi escrito para ser lido pelo **Claude Code**. Duas formas de uso:

1. **Recomendada**: salve este arquivo como `CLAUDE.md` na raiz do repositório do BIKESHARE. O Claude Code carrega esse arquivo automaticamente como contexto persistente em toda sessão nesse diretório.
2. **Alternativa**: cole o conteúdo inteiro como a primeira mensagem de uma tarefa para o Claude Code.

Se o repositório também contiver o arquivo `BIKESHARE_Especificacao_Projeto.md` (a especificação completa do projeto, com o diagrama de classes, as máquinas de estado e o roteiro do relatório), commite-o também — ele é a fonte de verdade para qualquer ambiguidade que este resumo não cubra. **Em caso de conflito, este `CLAUDE.md` manda nas convenções de teste/template; o `BIKESHARE_Especificacao_Projeto.md` manda no domínio/regras de negócio.**

> **Atualização (herança e polimorfismo):** esta versão incorpora as quatro hierarquias de herança do BIKESHARE (`Usuario`, `Bicicleta`, `MetodoPagamento`, `Penalidade`) e os pontos em que o design exige polimorfismo real (despacho dinâmico), não apenas Strategy. Duas peças novas resultam disso: a Seção 3.6 (parâmetros numéricos fixos, necessários para testes com `expect` determinístico) e a Seção 5.1 (como escrever testes que só passam se o aluno implementar corretamente os métodos sobrescritos em cada hierarquia — o EasyAccept não vê código, só comportamento observável).

---

## 1. Papel e objetivo

Você está atuando como engenheiro de suporte de uma disciplina de Programação 2 (UFAL), preparando o **template de partida** que será distribuído aos alunos, nos mesmos moldes do projeto **WePayU** usado em semestres anteriores. Seu objetivo tem duas partes:

**A. Gerar os scripts de teste de aceitação** (formato EasyAccept) para as User Stories 1 a 16 do BIKESHARE, cobrindo casos de sucesso, erro e persistência.

**B. Preparar o template Java** pronto para compilar e rodar esses testes via EasyAccept — mas **sem resolver o exercício pelos alunos**.

---

## 2. Regra de ouro (a mais importante deste documento)

> **O template NUNCA implementa lógica de negócio.** Ele só contém: estrutura de pastas, configuração de build, `Main.java` (que roda o EasyAccept sobre os arquivos de teste) e a classe `Facade` com **assinaturas de métodos vazias** (stubs) que lançam `UnsupportedOperationException("Ainda não implementado.")`.

Consequências diretas dessa regra:

- **Não crie** as classes `Usuario`, `Bicicleta`, `Estacao`, `Aluguel`, `MetodoPagamento`, `Penalidade`, `EstrategiaTarifa`, os estados, etc. Isso é o exercício — se você as criar, está entregando a solução de design pronta.
- **Não implemente** nenhum cálculo (tarifa, penalidade, disponibilidade). Os métodos da Facade devem compilar, mas lançar exceção quando chamados.
- **Em particular, não escreva** nenhuma versão (nem "provisória") dos métodos polimórficos listados na Seção 3.1.1 (`Bicicleta.calcularAdicional`, `MetodoPagamento.cobrar`, `Penalidade.calcularValor`, `Usuario.calcularValorAluguel`, e as sobrescritas de estado da Seção 3.2). Eles existem só como referência para você entender *por que* os testes da Seção 5.1 têm o formato que têm — quem os implementa é o aluno.
- É **esperado e correto** que, ao rodar `Main.java` sobre o template vazio, **todos os testes falhem**. Isso não é um bug do template — é o ponto de partida do aluno. Não tente fazer nenhum teste passar.
- Se em algum momento você perceber que, para deixar algo "compilando direito", precisaria tomar uma decisão de design que resolveria uma parte do problema (ex.: decidir a fórmula de uma tarifa), **pare e deixe como stub** — não decida por eles.

---

## 3. Especificação de domínio (resumo autoritativo)

### 3.1 Hierarquias de herança (ver diagrama completo em `BIKESHARE_Especificacao_Projeto.md`, Seção 3)

- `Usuario` (abstrata) → `UsuarioPessoaFisica` (cpf) / `UsuarioPessoaJuridica` (cnpj, razaoSocial)
- `Bicicleta` (abstrata) → `BicicletaConvencional` / `BicicletaEletrica` (nivelBateria)
- `MetodoPagamento` (abstrata) → `Cartao` / `Pix` / `SaldoPreCarregado`
- `Penalidade` (abstrata) → `PenalidadeAtraso` / `PenalidadeDano` / `PenalidadeUsoIndevido`
- `EstrategiaTarifa` (interface, Strategy — não herança): `TarifaAvulsa` / `TarifaAssinante` / planos customizados criados em tempo de execução (US12)
- `Estacao`, `Aluguel`, `SistemaBikeshare`: sem subtipos.

#### 3.1.1 Contratos polimórficos (referência para você, não implementar)

Estes métodos são o motivo de existir de cada hierarquia — cada subtipo os sobrescreve de forma diferente, e é *essa* diferença que os testes da Seção 5.1 verificam observando só as saídas da Facade. Não gere estes métodos nem as classes que os conteriam.

| Classe base | Método | Retorno esperado por subtipo |
|---|---|---|
| `Bicicleta` | `calcularAdicional(int minutos)` | `BicicletaConvencional` → `0`; `BicicletaEletrica` → `minutos * ADICIONAL_BICICLETA_ELETRICA_POR_MINUTO` |
| `MetodoPagamento` | `cobrar(double valor)` | `Cartao`/`Pix` → sempre aprova (sem gateway real); `SaldoPreCarregado` → aprova só se houver saldo, senão recusa |
| `Penalidade` | `calcularValor()` | cada subtipo calcula o valor de um jeito próprio (ver Seção 3.6) |
| `Usuario` | `calcularValorAluguel(Aluguel a)` | delega para `plano.calcular(a)` (Strategy) e decide *como* cobrar via `metodoPagamento.cobrar(...)` — a diferença entre `UsuarioPessoaFisica`/`UsuarioPessoaJuridica` está em quem paga, não em quanto |

### 3.2 Máquinas de estado (resumo — ver Seção 4 do documento de especificação para os diagramas completos)

- **Bicicleta**: `Disponivel → EmUso → Disponivel` (devolução sem defeito) ou `→ EmManutencao` (defeito relatado); `EmManutencao → Disponivel` (manutenção concluída); `Disponivel → Removida`.
- **Aluguel**: `EmAndamento → Finalizado` (devolução normal) / `→ Cancelado` (devolução dentro do prazo de carência, mesma estação) / `→ Expirado` (tempo máximo excedido).
- **Usuario**: `Ativo ↔ Inadimplente` (penalidade não quitada / quitada); `Ativo → Removido`.
- **Estação**: não tem máquina de estados própria — "vazia"/"parcial"/"cheia" é derivado da contagem de bicicletas.

### 3.3 Regras de negócio que os testes devem exercitar

1. Retirada só é permitida se o usuário está `Ativo`, sem aluguel em `EmAndamento`, a bicicleta está `Disponivel` e está de fato na estação informada.
2. Devolução só é permitida se a estação de destino tem vaga livre e o aluguel está `EmAndamento`.
3. Tarifa calculada na devolução: avulso paga por minuto; assinante tem franquia de minutos grátis + minuto excedente.
4. Devolução na mesma estação dentro de um prazo de carência curto (ex.: 2 min) → `Cancelado`, sem cobrança.
5. Aluguel que excede um tempo máximo (ex.: 24h) → `Expirado`, gera `Penalidade` automática e marca a bicicleta como `EmManutencao`.
6. Penalidade não quitada → usuário `Inadimplente` (bloqueia novas retiradas).
7. Devolução em estação cheia deve ser recusada (aluguel permanece `EmAndamento`).
8. Identificador de usuário/bicicleta/estação é gerado automaticamente pelo sistema.
9. `BicicletaEletrica` cobra adicional por minuto e não pode ser retirada com bateria abaixo de um mínimo configurável.
10. `UsuarioPessoaJuridica` tem cnpj/razaoSocial em vez de cpf.
11. Retirada com cobrança pendente exige `MetodoPagamento` válido cadastrado.

### 3.4 User Stories (numeração fixa — use estes números nos nomes dos arquivos de teste)

| US | Título |
|---|---|
| 1 | Cadastro de usuário (pessoa física / jurídica) |
| 2 | Consulta de usuário |
| 3 | Alteração de usuário (inclui método de pagamento) |
| 4 | Remoção de usuário |
| 5 | Cadastro de estação |
| 6 | Gestão de bicicletas na estação (convencional / elétrica) |
| 7 | Consulta de disponibilidade (bicicletas / vagas) |
| 8 | Retirada de bicicleta |
| 9 | Devolução de bicicleta |
| 10 | Consulta de histórico de aluguéis |
| 11 | Planos e tarifação (avulso / assinante) |
| 12 | Criação de novos planos de tarifação |
| 13 | Regras de bloqueio (inadimplência, estação cheia/vazia, bicicleta indisponível) |
| 14 | Lançamento e conclusão de manutenção |
| 15 | Relatórios (disponibilidade, faturamento) |
| 16 | Undo/redo |

> Nesta versão, as US 6/7 também cobrem a consulta genérica de atributos de bicicleta (`getAtributoBicicleta`, Seção 4) e a US13 passa a incluir o lançamento manual de penalidades por dano/uso indevido (`lancaPenalidade`, Seção 4) — ambos existem para viabilizar os testes de polimorfismo da Seção 5.1.

### 3.5 Milestones

- **Milestone 1 (AB1)**: US 1 a 9.
- **Milestone 2 (AB2)**: US 1 a 16 (todas).

### 3.6 Parâmetros de simulação (fixe estes valores antes de gerar os testes)

A especificação original deixou tarifas e limites como "configuráveis" (ex.: "franquia de 60 min", "prazo de carência de 2 min"). Isso é correto para o *design* do aluno, mas os testes de aceitação precisam de números concretos nas linhas `expect` — assim como o WePayU sempre usa valores fixos (`salario=23`, `comissao=0,05`). Use exatamente estes valores em todos os arquivos de teste, para que fiquem consistentes entre si:

| Parâmetro | Valor sugerido |
|---|---|
| Tarifa avulso (por minuto) | R$ 0,50 |
| Franquia do assinante (por corrida) | 60 minutos grátis |
| Assinante, minuto excedente à franquia | R$ 0,30 |
| Adicional da bicicleta elétrica (por minuto, sobre a tarifa base) | R$ 0,20 |
| Nível mínimo de bateria para permitir retirada | 15% |
| Prazo de carência para cancelamento (mesma estação) | 2 minutos |
| Tempo máximo de aluguel antes de expirar | 24 horas |
| Penalidade automática por expiração (`PenalidadeAtraso`) | R$ 10,00 fixo por ocorrência |
| `PenalidadeDano` / `PenalidadeUsoIndevido` | valor informado no momento do lançamento (`lancaPenalidade`, Seção 4) — não é fixo |

> Estes são **valores sugeridos, não uma exigência do enunciado** — ajuste-os livremente antes de rodar este prompt. O que importa é fixá-los *uma vez* e usá-los da mesma forma em todos os `us*.txt`, exatamente como o WePayU manteve `comissao=0,05` estável entre scripts de User Stories diferentes.

---

## 4. Contrato da Facade (linguagem de script → assinatura Java)

A classe `br.ufal.ic.p2.bikeshare.Facade` é o único ponto de entrada acessado pelo EasyAccept via reflexão. **O EasyAccept escolhe o método pelo nome + pela quantidade e ordem dos valores presentes na linha do script** (os nomes `chave=` são só para legibilidade — os valores são passados posicionalmente, na ordem em que aparecem na linha) — por isso, quando um comando aparece com combinações diferentes de parâmetros nos testes (ex.: `criarUsuario` com `cpf` vs. com `cnpj`+`razaoSocial`), a Facade precisa de uma sobrecarga Java para cada combinação usada. Gere as sobrecargas necessárias para cobrir exatamente as combinações que você usar nos scripts — mantenha Facade e testes sincronizados.

Tipo de retorno do script → tipo Java: `<void>` → `void`; `<String>` → `String` (stub: `throw new UnsupportedOperationException(...)`, nunca `return null` silenciosamente).

| Comando (script) | Assinatura Java equivalente (stub) |
|---|---|
| `zerarSistema` | `void zerarSistema()` |
| `criarUsuario nome=... email=... senha=... tipo=pessoaFisica cpf=...` | `String criarUsuario(String nome, String email, String senha, String tipo, String cpf)` |
| `criarUsuario ... tipo=pessoaJuridica cnpj=... razaoSocial=...` | `String criarUsuario(String nome, String email, String senha, String tipo, String cnpj, String razaoSocial)` |
| `criarUsuario ... plano=assinante mensalidade=...` (combinado com qualquer tipo acima) | sobrecarga correspondente com `+ String plano, String mensalidade` |
| `alterarUsuario usuario=... atributo=... valor=...` | `void alterarUsuario(String usuario, String atributo, String valor)` |
| `alterarUsuario usuario=... atributo=metodoPagamento valor1=cartao numero=... validade=...` | `void alterarUsuario(String usuario, String atributo, String valor1, String numero, String validade)` |
| `alterarUsuario ... valor1=pix chave=...` | `void alterarUsuario(String usuario, String atributo, String valor1, String chave)` |
| `alterarUsuario ... valor1=saldoPreCarregado` | `void alterarUsuario(String usuario, String atributo, String valor1)` |
| `removerUsuario usuario=...` | `void removerUsuario(String usuario)` |
| `getAtributoUsuario usuario=... atributo=...` | `String getAtributoUsuario(String usuario, String atributo)` |
| `getUsuarioPorEmail email=...` | `String getUsuarioPorEmail(String email)` |
| `criarEstacao nome=... endereco=... capacidade=...` | `String criarEstacao(String nome, String endereco, String capacidade)` |
| `adicionarBicicleta estacao=... bicicleta=... tipo=convencional` | `void adicionarBicicleta(String estacao, String bicicleta, String tipo)` |
| `adicionarBicicleta estacao=... bicicleta=... tipo=eletrica nivelBateria=...` | `void adicionarBicicleta(String estacao, String bicicleta, String tipo, String nivelBateria)` |
| `removerBicicleta estacao=... bicicleta=...` | `void removerBicicleta(String estacao, String bicicleta)` |
| `getBicicletasDisponiveis estacao=...` | `String getBicicletasDisponiveis(String estacao)` |
| `getVagasDisponiveis estacao=...` | `String getVagasDisponiveis(String estacao)` |
| `getEstadoBicicleta bicicleta=...` | `String getEstadoBicicleta(String bicicleta)` |
| `getAtributoBicicleta bicicleta=... atributo=...` | `String getAtributoBicicleta(String bicicleta, String atributo)` — viabiliza testar `atributo=tipo`, `atributo=nivelBateria` (erro esperado em `BicicletaConvencional`, ver Seção 5.1) e `atributo=estacao`. |
| `retirarBicicleta usuario=... estacao=... bicicleta=... data=...` | `String retirarBicicleta(String usuario, String estacao, String bicicleta, String data)` |
| `devolverBicicleta aluguel=... estacao=... data=...` | `void devolverBicicleta(String aluguel, String estacao, String data)` |
| `getAtributoAluguel aluguel=... atributo=...` | `String getAtributoAluguel(String aluguel, String atributo)` |
| `getHistoricoAlugueis usuario=...` | `String getHistoricoAlugueis(String usuario)` |
| `getHistoricoAlugueis usuario=... atributo=...` | `String getHistoricoAlugueis(String usuario, String atributo)` — sobrecarga usada nos testes para `atributo=quantidade`/`atributo=totalGasto`. |
| `lancaDefeito bicicleta=... descricao=...` | `void lancaDefeito(String bicicleta, String descricao)` |
| `concluiManutencao bicicleta=...` | `void concluiManutencao(String bicicleta)` |
| `lancaPenalidade usuario=... tipo=dano valor=... motivo=...` | `void lancaPenalidade(String usuario, String tipo, String valor, String motivo)` — única forma de gerar `PenalidadeDano`/`PenalidadeUsoIndevido` manualmente (`tipo=usoIndevido` usa a mesma assinatura); `PenalidadeAtraso` continua automática, pela Regra 5 da Seção 3.3. |
| `criarPlano nome=... franquiaMinutos=... valorMinutoExcedente=... mensalidade=...` | `String criarPlano(String nome, String franquiaMinutos, String valorMinutoExcedente, String mensalidade)` |
| `getTotalFaturado dataInicial=... dataFinal=...` | `String getTotalFaturado(String dataInicial, String dataFinal)` |
| `undo` | `void undo()` |
| `redo` | `void redo()` |
| `encerrarSistema` | `void encerrarSistema()` |

**Não invente comandos fora desta lista.** Se um teste que você for escrever precisar de uma variação de parâmetros não prevista aqui, adicione a sobrecarga correspondente na Facade, mas não crie um comando com nome novo sem necessidade.

---

## 5. Convenções de estilo dos scripts de teste

Baseadas nos scripts reais do WePayU (`TETEURYAN/WePayUPOO/tests`) — siga-as à risca para manter familiaridade com os alunos:

- **Cabeçalho de comentário** no topo de cada arquivo, no estilo:
  ```
  #####################################################################################
  # User Story N - <título>
  #####################################################################################
  ```
- **Comentários** com `#` explicando blocos de setup ou regras não óbvias.
- **Captura de variável**: `id1=criarUsuario ...` e reuso com `${id1}`.
- **Linhas longas**: quebradas com `\` no fim da linha, como no WePayU.
- **Asserções**: `expect <valorEsperado> <comando>`, `expectError "<mensagem exata>" <comando>`, `expectDifferent <valor> echo <comando>`.
- **Valores monetários**: formato brasileiro com vírgula, duas casas decimais (`12,50`, `0,00`) — nunca ponto.
- **Datas/horários**: use `dd/mm/aaaa hh:mm` (ex.: `10/3/2025 14:30`) em todos os comandos que envolvem tempo de aluguel (retirada/devolução), já que a cobrança do BIKESHARE depende de minutos, não só do dia. Para comandos que só precisam de data (ex.: `getTotalFaturado`), use `dd/mm/aaaa`. Valores com espaço precisam de aspas (`data="10/3/2025 14:30"`).
- **Arquivos "cheios"** (ex.: `us1.txt`) começam com `zerarSistema` e terminam com `encerrarSistema` seguido de `quit`.
- **Arquivos de persistência** (ex.: `us1_1.txt`) **não** chamam `zerarSistema` — eles continuam a partir do estado deixado pelo `encerrarSistema` do arquivo anterior, e verificam que os dados sobreviveram (poucas asserções, focadas em `get*`).
- **Granularidade esperada por arquivo** (calibrado nos testes reais do WePayU): arquivos simples de consulta → ~10–15 asserções; arquivos de CRUD com validação de erros → ~25–45 asserções; arquivos de regra de negócio complexa (retirada/devolução com tarifação, bloqueios, undo/redo) → 50–70+ asserções. Não entregue arquivos artificialmente curtos só para "ter o arquivo" — cubra os casos de erro de verdade (parâmetro vazio, entidade inexistente, atributo inválido, valor negativo, tipo inválido, etc.), um `expectError` por caso, com mensagens específicas e consistentes entre si.
- **Uma mensagem de erro por situação**, reaproveitada de forma consistente entre os arquivos (ex.: sempre `"Usuario nao existe."`, nunca variar a redação da mesma situação entre arquivos diferentes).

### 5.1 Testes que evidenciam herança e polimorfismo

O EasyAccept só vê a Facade por fora — não sabe se o aluno usou herança, `if/else` encadeado ou copiar-e-colar. A única forma de um teste "cobrar" polimorfismo de verdade é fazer com que a saída correta só seja possível se existir, de fato, uma implementação por subtipo. Estes quatro padrões (um por hierarquia) foram de fato aplicados nos scripts deste repositório:

1. **`Usuario`** (US1) — atributo válido só para o subtipo certo (`cpf` só em pessoa física, `cnpj`/`razaoSocial` só em pessoa jurídica, mensagens `"Usuario nao eh pessoa juridica."`/`"Usuario nao eh pessoa fisica."`).
2. **`MetodoPagamento`** (US3) — idem para `numeroCartao`/`chavePix`/`saldo`, e o caso de `SaldoPreCarregado` recusando a retirada com saldo zerado (`"Saldo insuficiente."`) enquanto `Cartao`/`Pix` sempre aprovam.
3. **`Bicicleta`** (US6/US9) — mesma corrida, tarifas diferentes: 10 minutos custam `5,00` numa convencional e `7,00` numa elétrica (a diferença só existe se `calcularAdicional` estiver de fato sobrescrito).
4. **`Penalidade`** (US13) — `PenalidadeAtraso` (automática, valor fixo de expiração) e `PenalidadeDano`/`PenalidadeUsoIndevido` (lançadas via `lancaPenalidade`, valor informado) somando corretamente em `totalPendencias`.

### Exemplo de estilo esperado (trecho ilustrativo, não é o arquivo final)

```
#####################################################################################
# User Story 1 - Cadastro de usuario
#####################################################################################

zerarSistema
expectError "Usuario nao existe." getAtributoUsuario usuario=abc atributo=nome

id1=criarUsuario nome="Ana Souza" email="ana@exemplo.com" senha="123456" tipo=pessoaFisica cpf=111.222.333-44
expect "Ana Souza" getAtributoUsuario usuario=${id1} atributo=nome
expect pessoaFisica getAtributoUsuario usuario=${id1} atributo=tipo
expect ativo getAtributoUsuario usuario=${id1} atributo=estado

id2=criarUsuario nome="Bike Corp Ltda" email="contato@bikecorp.com" senha="senha123" \
    tipo=pessoaJuridica cnpj=12.345.678/0001-99 razaoSocial="Bike Corp Ltda"
expect pessoaJuridica getAtributoUsuario usuario=${id2} atributo=tipo
expect "Bike Corp Ltda" getAtributoUsuario usuario=${id2} atributo=razaoSocial

expectError "Nome nao pode ser nulo." criarUsuario nome="" email="x@x.com" senha="123456" tipo=pessoaFisica cpf=111.222.333-44
expectError "Tipo invalido." criarUsuario nome="X" email="x@x.com" senha="123456" tipo=abc cpf=111.222.333-44

encerrarSistema
quit
```

---

## 6. O que construir — checklist de entrega

### 6.1 Estrutura de pastas (espelhando o WePayUPOO)

```
BIKESHARE/
 ├── src/
 │    └── br/ufal/ic/p2/bikeshare/
 │         ├── Main.java
 │         └── Facade.java        (stub — ver Seção 2 e 4)
 ├── tests/
 │    ├── us1.txt ... us16.txt
 │    └── us1_1.txt, us3_1.txt, ... (variações de persistência/erro, conforme necessário)
 ├── lib/
 │    └── (easyaccept.jar — ver nota abaixo)
 ├── relatorio/
 │    └── .gitkeep
 └── README.md
```

> **Nota sobre `easyaccept.jar`**: se você tiver acesso à internet no ambiente de execução, baixe o jar oficial do EasyAccept e coloque em `lib/`. Se não tiver, **não invente/gere um jar falso** — deixe uma instrução clara no `README.md` explicando onde os alunos/monitor devem obter o `easyaccept.jar` (ex.: repositório da disciplina) e como configurá-lo como biblioteca no IntelliJ.

### 6.2 `Main.java`

Deve rodar o EasyAccept sobre **todos** os arquivos de teste gerados, na ordem das User Stories, no mesmo estilo do `Main.java` do WePayU (uma chamada `EasyAccept.main(...)` por arquivo, incluindo os arquivos `_1`/persistência logo após seu arquivo "cheio" correspondente).

### 6.3 `Facade.java`

Todos os métodos da Seção 4, agrupados por User Story em comentários, cada um lançando `UnsupportedOperationException`. Nenhuma outra classe do domínio deve existir no template.

### 6.4 Testes de aceitação

Gere `us1.txt` a `us16.txt`, cada um com o par de persistência (`usN_1.txt`) quando fizer sentido pela regra da Seção 5. Siga a numeração da Seção 3.4 — não renumere as User Stories. Ao menos um arquivo por hierarquia de herança deve seguir os padrões da Seção 5.1 (evidenciar comportamento diferenciado entre subtipos, não só atributos estáticos).

### 6.5 `README.md`

Breve, explicando: como abrir o projeto no IntelliJ, onde colocar `easyaccept.jar`, como rodar `Main.java`, e o aviso explícito de que **é esperado que todos os testes falhem** no estado inicial do template.

---

## 7. Ordem de execução sugerida

1. Criar a estrutura de pastas e o `README.md`.
2. Criar `Facade.java` com todos os stubs da Seção 4.
3. Criar `Main.java` referenciando (ainda vazio de conteúdo, apenas a lista) os arquivos de teste que serão criados.
4. Gerar os testes do **Milestone 1** (US 1 a 9), ajustando as sobrecargas da Facade conforme necessário.
5. Gerar os testes do **Milestone 2** (US 10 a 16), ajustando as sobrecargas da Facade conforme necessário.
6. Atualizar `Main.java` com a lista final e completa de arquivos.
7. Validar que o projeto **compila** (`javac` ou build do IntelliJ) — não é necessário nem esperado que os testes passem.
8. Revisar se toda mensagem de `expectError` usada nos testes tem uma redação consistente entre arquivos diferentes.

## 8. Critérios de aceite (definition of done)

- [x] Projeto compila sem erros.
- [x] `Main.java` executa e roda o EasyAccept sobre todos os arquivos (as falhas são esperadas e aceitáveis).
- [x] Todo comando usado em algum arquivo `.txt` tem uma assinatura correspondente (stub) na `Facade`.
- [x] Nenhuma classe de modelo/lógica de negócio foi criada além da `Facade` stub e do `Main`.
- [x] Valores monetários com vírgula, datas/horários no formato `dd/mm/aaaa hh:mm`.
- [x] Cada User Story tem pelo menos um arquivo "cheio"; as que envolvem estado persistente relevante (cadastro, alteração, remoção, retirada/devolução, planos, manutenção) têm também o arquivo `_1` de persistência.
- [x] `README.md` criado com instruções de uso e o aviso sobre o estado inicial (tudo falha).
- [x] Cada uma das quatro hierarquias de herança (`Usuario`, `Bicicleta`, `MetodoPagamento`, `Penalidade`) tem pelo menos um teste no estilo da Seção 5.1, evidenciando comportamento diferenciado entre subtipos via saída da Facade.

## 9. O que NÃO fazer

- Não implemente `Usuario`, `Bicicleta`, `Estacao`, `Aluguel`, estados, estratégias de tarifa, penalidades, ou qualquer lógica de negócio.
- Não implemente os métodos polimórficos da Seção 3.1.1, nem "adiante" a fórmula de nenhum deles em comentário no stub.
- Não tente fazer nenhum teste passar.
- Não invente comandos além dos listados na Seção 4.
- Não use Maven/Gradle a menos que explicitamente solicitado — mantenha o projeto simples, no estilo IntelliJ + `lib/`, igual ao WePayUPOO.
- Não renumere ou renomeie as User Stories da Seção 3.4.
- Não gere um `easyaccept.jar` falso/vazio só para o projeto "compilar" — documente a ausência no `README.md` em vez disso.

---

## 10. Nota sobre a implementação de referência

Existe uma implementação de referência completa (todas as 16 User Stories, com persistência e undo/redo), usada apenas para validar que os 26 arquivos de teste em `tests/` são consistentes entre si e com as mensagens de erro — ela vive num repositório **separado e privado** (`bikeshare-solved`), não neste. Este repositório (`bikeshare`) deve permanecer sempre com a Façade em stub, pronta para ser distribuída aos alunos.
