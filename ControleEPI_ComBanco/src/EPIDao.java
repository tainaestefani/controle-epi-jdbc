import java.sql.*;
import java.util.ArrayList;

public class EPIDao {
    public void inserirEPI(EPI epi) {
        String sql = "INSERT INTO epi (nome, validade) VALUES (?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, epi.getNome());
            stmt.setString(2, epi.getValidade());
            stmt.executeUpdate();
            System.out.println("EPI inserido com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir EPI: " + e.getMessage());
        }
    }

    public ArrayList<EPI> listarEPIs() {
        ArrayList<EPI> lista = new ArrayList<>();
        String sql = "SELECT * FROM epi";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EPI epi = new EPI(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("validade")
                );
                lista.add(epi);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar EPIs: " + e.getMessage());
        }
        return lista;
    }
}