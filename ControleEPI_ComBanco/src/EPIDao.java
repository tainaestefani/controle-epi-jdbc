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

    public EPI buscarEPIporId(int id) {
        String sql = "SELECT * FROM epi WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EPI(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("validade")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar EPI: " + e.getMessage());
        }
        return null;
    }

    public void atualizarEPI(EPI epi) {
        String sql = "UPDATE epi SET nome = ?, validade = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, epi.getNome());
            stmt.setString(2, epi.getValidade());
            stmt.setInt(3, epi.getId());
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("EPI atualizado com sucesso!");
            } else {
                System.out.println("Nenhum EPI encontrado com o ID informado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar EPI: " + e.getMessage());
        }
    }

    public void deletarEPI(int id) {
        String sql = "DELETE FROM epi WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("EPI deletado com sucesso!");
            } else {
                System.out.println("Nenhum EPI encontrado com o ID informado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao deletar EPI: " + e.getMessage());
        }
    }
}