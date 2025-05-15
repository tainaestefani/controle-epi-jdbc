package gerenciadores;

import entidades.Emprestimo;
import entidades.Epi;
import entidades.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class GerenciadorEmprestimo {

    private Scanner scanner = new Scanner(System.in);
    GerenciadorUsuario gerenciadorUsuario;
    GerenciadorEpi gerenciadorEpi;

    public GerenciadorEmprestimo(GerenciadorUsuario gerenciadorUsuario, GerenciadorEpi gerenciadorEpi) {
        this.gerenciadorUsuario = gerenciadorUsuario;
        this.gerenciadorEpi = gerenciadorEpi;
    }

    public void criarEmprestimo() {
        try {
            System.out.println("Colaboradores: ");
            Usuario usuario = gerenciadorUsuario.buscarUsuario();

            System.out.println("EPIs:");
            Epi epi = gerenciadorEpi.buscarEpi();

            if (usuario != null && epi != null) {
                if (!validarEmprestimo(epi)) throw new RuntimeException("Não há " + epi.getNome() + " suficientes");

                System.out.print("Digite a data de devolução prevista (DD/MM/AAAA): ");
                LocalDate dataDevolucao = buscarData();

                String sql = "INSERT INTO emprestimos (usuario_id, epi_id, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, usuario.getId());
                    stmt.setInt(2, epi.getId());
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.setDate(4, Date.valueOf(dataDevolucao));
                    stmt.executeUpdate();
                }

                epi.setQuantidade(epi.getQuantidade() - 1);

                System.out.println("Empréstimo criado com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar empréstimo: " + e.getMessage());
        }
    }

    public boolean validarEmprestimo(Epi epi) {
        return epi.getQuantidade() > 0;
    }

    public void listarEmprestimos() {
        String sql = "SELECT * FROM emprestimos";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery(sql)) {
            int i = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                int usuarioId = rs.getInt("usuario_id");
                int epiId = rs.getInt("epi_id");
                LocalDate dataEmprestimo = rs.getDate("data_emprestimo").toLocalDate();
                LocalDate dataDevolucao = rs.getDate("data_devolucao").toLocalDate();

                Usuario usuario = gerenciadorUsuario.buscarUsuarioPorId(usuarioId);
                Epi epi = gerenciadorEpi.buscarEpiPorId(epiId);

                Emprestimo emprestimo = new Emprestimo(id, epi, usuario, dataEmprestimo, dataDevolucao);
                System.out.println(emprestimo.getId() + ": " + emprestimo);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar empréstimos: " + e.getMessage());
        }
    }

    public Emprestimo buscarEmprestimo() {
        listarEmprestimos();
        System.out.print("Digite o ID do empréstimo: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();

            String sql = "SELECT * FROM emprestimos WHERE id = ?";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int usuarioId = rs.getInt("usuario_id");
                        int epiId = rs.getInt("epi_id");
                        LocalDate dataEmprestimo = rs.getDate("data_emprestimo").toLocalDate();
                        LocalDate dataDevolucao = rs.getDate("data_devolucao").toLocalDate();

                        Usuario usuario = gerenciadorUsuario.buscarUsuarioPorId(usuarioId);
                        Epi epi = gerenciadorEpi.buscarEpiPorId(epiId);

                        return new Emprestimo(id, epi, usuario, dataEmprestimo, dataDevolucao);
                    } else {
                        System.out.println("Empréstimo não encontrado.");
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimo: " + e.getMessage());
            scanner.nextLine();
            return null;
        }
    }
    public Emprestimo buscarEmprestimoPorId(int id) {
        String sql = "SELECT * FROM emprestimos WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("usuario_id");
                    int epiId = rs.getInt("epi_id");
                    LocalDate dataEmprestimo = rs.getDate("data_emprestimo").toLocalDate();
                    LocalDate dataDevolucao = rs.getDate("data_devolucao").toLocalDate();

                    Usuario usuario = gerenciadorUsuario.buscarUsuarioPorId(usuarioId);
                    Epi epi = gerenciadorEpi.buscarEpiPorId(epiId);

                    return new Emprestimo(id, epi, usuario, dataEmprestimo, dataDevolucao);
                } else {
                    System.out.println("Empréstimo não encontrado.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar empréstimo: " + e.getMessage());
            return null;
        }
    }


    public LocalDate buscarData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Entrada vazia. Tente novamente: ");
                    continue;
                }
                return LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.print("Formato inválido. Use DD/MM/AAAA. Digite novamente: ");
            }
        }
    }

    public void atualizarEmprestimo() {
        Emprestimo emprestimo = buscarEmprestimo();
        if (emprestimo == null) return;

        try {
            System.out.print("1. Atualizar usuário\n" +
                    "2. Atualizar EPI\n" +
                    "3. Atualizar data de empréstimo\n" +
                    "4. Atualizar data de devolução\n" +
                    "5. Voltar\nDigite uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> emprestimo.setUsuario(gerenciadorUsuario.buscarUsuario());
                case 2 -> {
                    Epi novaEpi = gerenciadorEpi.buscarEpi();
                    if (!validarEmprestimo(novaEpi)) throw new RuntimeException("Não há " + novaEpi.getNome() + " suficientes");

                    novaEpi.setQuantidade(novaEpi.getQuantidade() - 1);
                    emprestimo.setEpi(novaEpi);
                }
                case 3 -> {
                    System.out.print("Digite a nova data de empréstimo: ");
                    emprestimo.setDataEmprestimo(buscarData());
                }
                case 4 -> {
                    System.out.print("Digite a nova data de devolução: ");
                    emprestimo.setDataDevolucao(buscarData());
                }
                case 5 -> {
                    return;
                }
                default -> {
                    System.out.println("Opção inválida.");
                    return;
                }
            }

            String sql = "UPDATE emprestimos SET usuario_id = ?, epi_id = ?, data_emprestimo = ?, data_devolucao = ? WHERE id = ?";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, emprestimo.getUsuario().getId());
                stmt.setInt(2, emprestimo.getEpi().getId());
                stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
                stmt.setDate(4, Date.valueOf(emprestimo.getDataDevolucao()));
                stmt.setInt(5, emprestimo.getId());
                stmt.executeUpdate();
            }

            System.out.println("Empréstimo atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar empréstimo: " + e.getMessage());
        }
    }

    public void removerEmprestimo() {
        Emprestimo emprestimo = buscarEmprestimo();
        if (emprestimo == null) return;

        try {
            String sql = "DELETE FROM emprestimos WHERE id = ?";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, emprestimo.getId());
                stmt.executeUpdate();
            }

            Epi epi = emprestimo.getEpi();
            epi.setQuantidade(epi.getQuantidade() + 1);

            System.out.println("Empréstimo removido com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao remover empréstimo: " + e.getMessage());
        }
    }
}