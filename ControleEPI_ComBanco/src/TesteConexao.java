public class TesteConexao {
    public static void main(String[] args) {
        if (Conexao.conectar() != null) {
            System.out.println("Conexão realizada com sucesso!");
        } else {
            System.out.println("Falha na conexão.");
        }
    }
}