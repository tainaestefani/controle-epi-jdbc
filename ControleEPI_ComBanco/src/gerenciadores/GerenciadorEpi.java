package gerenciadores;

import entidades.Epi;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GerenciadorEpi {
    Scanner scanner = new Scanner(System.in);

    public void cadastrarEpi() {
        try {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Quantidade: ");
            int quantidade = scanner.nextInt();
            scanner.nextLine();

            String sql = "INSERT INTO epis (nome, quantidade) VALUES (?, ?)";

            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, nome);
                stmt.setInt(2, quantidade);
                stmt.executeUpdate();

                System.out.println("EPI cadastrada com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar EPI: " + e.getMessage());
            scanner.nextLine();
        }
    }

    public List<Epi> listarEpis() {
        List<Epi> epis = new ArrayList<>();
        String sql = "SELECT * FROM epis";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Epi epi = new Epi();
                epi.setId(rs.getInt("id_epi"));
                epi.setNome(rs.getString("nome"));
                epi.setQuantidade(rs.getInt("quantidade"));
                epis.add(epi);
            }

            if (epis.isEmpty()) {
                System.out.println("Não há EPIs cadastradas.");
            } else {
                for (int i = 0; i < epis.size(); i++) {
                    System.out.println(epis.get(i).getId() + ": " + epis.get(i));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar EPIs: " + e.getMessage());
        }

        return epis;
    }

    public Epi buscarEpi() {
        List<Epi> epis = listarEpis();
        if (epis.isEmpty()) return null;

        while (true) {
            try {
                System.out.print("Digite o índice do EPI: ");
                int indice = scanner.nextInt();
                scanner.nextLine();

                return buscarEpiPorId(indice);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Índice inválido. Tente novamente.");
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
                scanner.nextLine();
            }
        }
    }
    public Epi buscarEpiPorId(int id) {
        Epi epi = null;
        String sql = "SELECT * FROM epis WHERE id_epi = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    epi = new Epi();
                    epi.setId(rs.getInt("id_epi"));
                    epi.setNome(rs.getString("nome"));
                    epi.setQuantidade(rs.getInt("quantidade"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar EPI por ID: " + e.getMessage());
        }

        return epi;
    }


    public void atualizarEpi() {
        Epi epi = buscarEpi();
        if (epi == null) return;

        while (true) {
            try {
                System.out.print("1. Atualizar nome \n2. Atualizar quantidade\n3. Voltar\nDigite uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine();

                String sql = "";
                if (opcao == 1) {
                    System.out.print("Novo nome: ");
                    String novoNome = scanner.nextLine();
                    sql = "UPDATE epis SET nome = ? WHERE id_epi = ?";
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, novoNome);
                        stmt.setInt(2, epi.getId());
                        stmt.executeUpdate();
                        System.out.println("Nome atualizado com sucesso!");
                    }
                } else if (opcao == 2) {
                    System.out.print("Nova quantidade: ");
                    int novaQtd = scanner.nextInt();
                    scanner.nextLine();
                    sql = "UPDATE epis SET quantidade = ? WHERE id_epi = ?";
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, novaQtd);
                        stmt.setInt(2, epi.getId());
                        stmt.executeUpdate();
                        System.out.println("Quantidade atualizada com sucesso!");
                    }
                } else if (opcao == 3) break;
                else {
                    System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro ao atualizar EPI: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    public void removerEpi() {
        Epi epi = buscarEpi();
        if (epi == null) return;

        String verificaEmprestimoSql = "SELECT COUNT(*) FROM epis WHERE id_epi = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(verificaEmprestimoSql)) {

            stmt.setInt(1, epi.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Não é possível remover o EPI! Ele está sendo utilizado em um ou mais empréstimos.");
                }
            }

            String sql = "DELETE FROM epi WHERE id_epi = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setInt(1, epi.getId());
                deleteStmt.executeUpdate();
                System.out.println("EPI removido com sucesso!");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao remover EPI: " + e.getMessage());
        }
    }

}