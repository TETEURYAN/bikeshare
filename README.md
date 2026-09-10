# BIKESHARE

Template de partida do projeto **BIKESHARE** (Programação 2 — UFAL), nos mesmos
moldes do projeto **WePayU** usado em semestres anteriores: um sistema de
gerenciamento de bicicletas compartilhadas, acessado por uma única `Facade` e
validado por scripts de teste de aceitação executados com o **EasyAccept**.

A especificação completa do domínio está em
[`BIKESHARE_Especificacao_Projeto.md`](BIKESHARE_Especificacao_Projeto.md).
As convenções de teste e o contrato da `Facade` estão em [`CLAUDE.md`](CLAUDE.md).

## ⚠️ Estado deste template

Este repositório contém **apenas o esqueleto do projeto**: estrutura de pastas,
configuração de build, `Main.java` e uma `Facade` cujos métodos têm a
assinatura correta mas **não fazem nada** além de lançar
`UnsupportedOperationException("Ainda não implementado.")`.

**É esperado e correto que, ao rodar `Main.java` neste estado, todos os
testes de aceitação falhem.** Isso não é um bug do template — é o ponto de
partida. O trabalho da disciplina consiste em implementar as classes de
domínio (`Usuario`, `Bicicleta`, `Estacao`, `Aluguel`, `MetodoPagamento`,
`Penalidade`, as estratégias de tarifa, os estados, undo/redo e persistência)
por trás dessa `Facade`, fazendo os testes passarem um a um.

## Estrutura do repositório

```
BIKESHARE/
 ├── src/
 │    ├── Main.java                          (roda o EasyAccept sobre tests/*.txt)
 │    └── br/ufal/ic/p2/bikeshare/
 │         └── Facade.java                   (stub — implemente por trás dela)
 ├── tests/
 │    └── us1.txt ... us16.txt (+ variações _1 de persistência)
 ├── lib/
 │    └── easyaccept.jar
 ├── relatorio/                              (relatório final, em PDF)
 ├── CLAUDE.md                                (contrato da Facade e convenções de teste)
 └── BIKESHARE_Especificacao_Projeto.md       (especificação de domínio completa)
```

## Como abrir no IntelliJ

1. **File → Open...** e selecione a pasta `BIKESHARE` (a raiz deste repositório).
2. Marque `src` como *Sources Root* (botão direito sobre `src` → *Mark Directory as → Sources Root*), caso o IntelliJ não detecte automaticamente.
3. Adicione `lib/easyaccept.jar` como biblioteca do projeto: **File → Project Structure → Libraries → + → Java**, selecione `lib/easyaccept.jar` e aplique ao módulo do projeto.
   - Alternativamente: botão direito sobre `lib/easyaccept.jar` no explorador de arquivos → *Add as Library...*.
4. Rode a classe `Main` (botão direito sobre `src/Main.java` → *Run 'Main.main()'*).

## Onde conseguir o `easyaccept.jar`

Este repositório já inclui `lib/easyaccept.jar`. Caso precise obter uma cópia
por conta própria (por exemplo, se o arquivo não estiver disponível no seu
clone), peça ao monitor/professor da disciplina o jar oficial usado nos
projetos anteriores (o mesmo `easyaccept.jar` do WePayU) e coloque-o em `lib/`.

## Rodando os testes pela linha de comando

```bash
javac -cp lib/easyaccept.jar -d out $(find src -name "*.java")
java -cp "lib/easyaccept.jar:out" Main
```

No estado inicial deste template, a saída mostrará falhas em praticamente
todas as linhas `expect`/`expectError` de todos os 26 arquivos em `tests/` —
isso é esperado. Conforme as classes de domínio forem implementadas por trás
da `Facade`, os testes devem passar a virar `OK`, primeiro para as User
Stories 1 a 9 (Milestone 1 / AB1) e depois para as demais (Milestone 2 / AB2).

## Convenções dos scripts de teste

- Datas/horários de aluguel: `dd/M/aaaa H:mm` (ex.: `10/3/2025 14:30`); datas
  isoladas (relatórios): `dd/M/aaaa`.
- Valores monetários: formato brasileiro, com vírgula e duas casas decimais
  (ex.: `12,50`).
- Arquivos "cheios" (`usN.txt`) começam com `zerarSistema` e terminam com
  `encerrarSistema` seguido de `quit`; arquivos de persistência (`usN_1.txt`)
  continuam a partir do estado salvo pelo arquivo anterior, sem `zerarSistema`.

Veja o [`CLAUDE.md`](CLAUDE.md) para o contrato completo da `Facade`, os
parâmetros de tarifação fixados para os testes e os padrões usados para
evidenciar herança/polimorfismo nos scripts.
