package gerenciadores;

import entidades.Devolucao;
import entidades.Emprestimo;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GerenciadorDevolucao {
    Scanner scanner = new Scanner(System.in);
    private List<Devolucao> devolucoes;

    GerenciadorEmprestimo gerenciadorEmprestimo;

    public GerenciadorDevolucao(GerenciadorEmprestimo gerenciadorEmprestimo) {
        devolucoes = new ArrayList<>();
        this.gerenciadorEmprestimo = gerenciadorEmprestimo;
    }

    public void criarDevolucao() {
        System.out.println("Empréstimo: ");
        Emprestimo emprestimo = gerenciadorEmprestimo.buscarEmprestimo();

        try {
            System.out.print("Digite a data de devolução (DD/MM/AAAA): ");
            LocalDate data = gerenciadorEmprestimo.buscarData();

            String sql = "INSERT INTO devolucoes (id_emprestimo, data_devolucao) VALUES (?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, emprestimo.getId());
                stmt.setDate(2, Date.valueOf(data));
                stmt.executeUpdate();

                System.out.println("Devolução criada com sucesso!");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Devolucao> listarDevolucoes() {
        List<Devolucao> devolucoes = new ArrayList<>();
        String sql = "SELECT * FROM devolucoes";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_devolucao");
                int idEmprestimo = rs.getInt("id_emprestimo");
                LocalDate dataDevolucao = rs.getDate("data_devolucao").toLocalDate();

                Emprestimo emprestimo = gerenciadorEmprestimo.buscarEmprestimoPorId(idEmprestimo);
                Devolucao devolucao = new Devolucao(id, emprestimo, dataDevolucao);
                devolucoes.add(devolucao);
            }

            if (devolucoes.isEmpty()) {
                System.out.println("Não há devoluções cadastradas");
            } else {
                devolucoes.forEach(devolucao -> System.out.println((devolucao.getId() + ": " + devolucao)));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar devoluções: " + e.getMessage());
        }

        return devolucoes;
    }

    public Devolucao buscarDevolucao() {
        while (true) {
            try {
                List<Devolucao> devolucoes = listarDevolucoes();
                if (devolucoes.isEmpty()) return null;

                System.out.print("Digite o índice da devolução: ");
                int indice = scanner.nextInt();
                scanner.nextLine();

                Devolucao devolucao = devolucoes.get(indice - 1);
                if (devolucao == null) throw new IllegalArgumentException("Erro ao encontrar a devolução. Tente novamente!");
                return devolucao;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Índice inválido. Tente novamente.");
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /*public void atualizarDevolucao() {
        Devolucao devolucao = buscarDevolucao();
        while (true) {
            try {
                if (devolucao == null) break;

                System.out.print("1. Atualizar empréstimo\n2. Atualizar data de devolução\n3. Voltar\nDigite uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine();

                String sql = "";
                if (opcao == 1) {
                    Emprestimo emprestimo = gerenciadorEmprestimo.buscarEmprestimo();
                    devolucao.setEmprestimo(emprestimo);

                    sql = "UPDATE devolucoes SET id_emprestimo = ? WHERE id_devolucao = ?";
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, emprestimo.getId());
                        stmt.setInt(2, devolucao.getId());
                        stmt.executeUpdate();
                        System.out.println("Empréstimo atualizado com sucesso!");
                    }

                } else if (opcao == 2) {
                    System.out.print("Digite a nova data de devolução (DD/MM/AAAA): ");
                    String inputData = scanner.nextLine();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate novaData = LocalDate.parse(inputData, formatter);

                    sql = "UPDATE devolucoes SET data_devolucao = ? WHERE id_devolucao = ?";
                    try (Connection conn = Conexao.conectar();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setDate(1, Date.valueOf(novaData));
                        stmt.setInt(2, devolucao.getId());
                        stmt.executeUpdate();
                        System.out.println("Data de devolução atualizada com sucesso!");
                    }

                } else if (opcao == 3) break;
                else {
                    throw new IllegalArgumentException("Opção inválida. Tente novamente!");
                }

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void removerDevolucao() {
        try {
            Devolucao devolucao = buscarDevolucao();
            if (devolucao != null) {
                String sql = "DELETE FROM devolucoes WHERE id_devolucao = ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, devolucao.getId());
                    stmt.executeUpdate();
                    System.out.println("Devolução removida com sucesso!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }*/
}