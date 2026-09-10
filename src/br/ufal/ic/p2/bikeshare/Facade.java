package br.ufal.ic.p2.bikeshare;

/**
 * Unico ponto de entrada da logica de negocio, acessado pelo EasyAccept via
 * reflexao. Este e o TEMPLATE distribuido aos alunos: nenhum metodo faz nada
 * alem de sinalizar que ainda precisa ser implementado — nao ha classes de
 * modelo, nao ha calculo de tarifa, nao ha persistencia. E esperado que, ao
 * rodar Main.java sobre este template, TODOS os testes de aceitacao falhem.
 *
 * As assinaturas (incluindo as sobrecargas) seguem exatamente o contrato
 * descrito no CLAUDE.md, Secao 4, e correspondem aos comandos usados nos
 * scripts em tests/*.txt.
 */
public class Facade {

    private static final String AINDA_NAO_IMPLEMENTADO = "Ainda não implementado.";

    // =====================================================================
    // US1 - Cadastro de usuario
    // =====================================================================

    public String criarUsuario(String nome, String email, String senha, String tipo, String cpfOuCnpj) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String criarUsuario(String nome, String email, String senha, String tipo, String cnpj, String razaoSocial) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String criarUsuario(String nome, String email, String senha, String tipo, String cpf,
                                String plano, String mensalidade) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String criarUsuario(String nome, String email, String senha, String tipo, String cnpj,
                                String razaoSocial, String plano, String mensalidade) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US2 - Consulta de usuario
    // =====================================================================

    public String getAtributoUsuario(String usuario, String atributo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getUsuarioPorEmail(String email) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US3 - Alteracao de usuario (inclui metodo de pagamento)
    // =====================================================================

    public void alterarUsuario(String usuario, String atributo, String valor) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void alterarUsuario(String usuario, String atributo, String valor1, String chave) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void alterarUsuario(String usuario, String atributo, String valor1, String numero, String validade) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US4 - Remocao de usuario
    // =====================================================================

    public void removerUsuario(String usuario) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US5 - Cadastro de estacao
    // =====================================================================

    public String criarEstacao(String nome, String endereco, String capacidade) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US6 - Gestao de bicicletas na estacao
    // =====================================================================

    public void adicionarBicicleta(String estacao, String bicicleta, String tipo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void adicionarBicicleta(String estacao, String bicicleta, String tipo, String nivelBateria) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void removerBicicleta(String estacao, String bicicleta) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getAtributoBicicleta(String bicicleta, String atributo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US7 - Consulta de disponibilidade
    // =====================================================================

    public String getBicicletasDisponiveis(String estacao) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getVagasDisponiveis(String estacao) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getEstadoBicicleta(String bicicleta) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US8 - Retirada de bicicleta
    // =====================================================================

    public String retirarBicicleta(String usuario, String estacao, String bicicleta, String data) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US9 - Devolucao de bicicleta
    // =====================================================================

    public void devolverBicicleta(String aluguel, String estacao, String data) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US10 - Consulta de historico de alugueis
    // =====================================================================

    public String getHistoricoAlugueis(String usuario) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getHistoricoAlugueis(String usuario, String atributo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public String getAtributoAluguel(String aluguel, String atributo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US11/US12 - Planos e tarifacao / criacao de novos planos
    // =====================================================================

    public String criarPlano(String nome, String franquiaMinutos, String valorMinutoExcedente, String mensalidade) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US13 - Regras de bloqueio / lancamento manual de penalidade
    // =====================================================================

    public void lancaPenalidade(String usuario, String tipo, String valor, String motivo) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US14 - Lancamento e conclusao de manutencao
    // =====================================================================

    public void lancaDefeito(String bicicleta, String descricao) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void concluiManutencao(String bicicleta) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US15 - Relatorios
    // =====================================================================

    public String getTotalFaturado(String dataInicial, String dataFinal) {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // US16 - Undo/redo
    // =====================================================================

    public void undo() {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void redo() {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    // =====================================================================
    // Ciclo de vida do sistema
    // =====================================================================

    public void zerarSistema() {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }

    public void encerrarSistema() {
        throw new UnsupportedOperationException(AINDA_NAO_IMPLEMENTADO);
    }
}
