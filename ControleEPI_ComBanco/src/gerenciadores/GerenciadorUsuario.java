package gerenciadores;

import entidades.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GerenciadorUsuario {
    Scanner scanner = new Scanner(System.in);

    public void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("E-mail: ");
        String email = scanner.nextLine().toLowerCase();

        String sql = "INSERT INTO usuarios (nome, email) VALUES (?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.executeUpdate();

            System.out.println("Usuário cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuarios.add(usuario);
            }

            if (usuarios.isEmpty()) {
                System.out.println("Não há usuários cadastrados.");
            } else {
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.println(usuarios.get(i).getId() + ": " + usuarios.get(i));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }

        return usuarios;
    }

    public Usuario buscarUsuario() {
        List<Usuario> usuarios = listarUsuarios();
        if (usuarios.isEmpty()) return null;

        while (true) {
            try {
                System.out.print("Digite o índice do usuário: ");
                int indice = scanner.nextInt();
                scanner.nextLine();

                return buscarUsuarioPorId(indice);
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
                scanner.nextLine();
            }
        }
    }
    public Usuario buscarUsuarioPorId(int id) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }

        return usuario;
    }


    public void atualizarUsuario() {
        Usuario usuario = buscarUsuario();
        if (usuario == null) return;

        while (true) {
            try {
                System.out.print("1. Atualizar nome\n2. Atualizar e-mail\n3. Voltar\nDigite uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine();

                if (opcao == 3) break;

                String sql = "";
                if (opcao == 1) {
                    System.out.print("Novo nome: ");
                    usuario.setNome(scanner.nextLine());
                    sql = "UPDATE usuarios SET nome = ? WHERE id_usuario = ?";
                } else if (opcao == 2) {
                    System.out.print("Novo e-mail: ");
                    usuario.setEmail(scanner.nextLine().toLowerCase());
                    sql = "UPDATE usuarios SET email = ? WHERE id_usuario = ?";
                } else {
                    System.out.println("Opção inválida.");
                    continue;
                }

                try (Connection conn = Conexao.conectar();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, opcao == 1 ? usuario.getNome() : usuario.getEmail());
                    stmt.setInt(2, usuario.getId());
                    stmt.executeUpdate();
                    System.out.println("Usuário atualizado com sucesso!\n");
                } catch (SQLException e) {
                    System.out.println("Erro ao atualizar usuário: " + e.getMessage());
                }

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida.");
                scanner.nextLine();
            }
        }
    }

    public void removerUsuario() {
        Usuario usuario = buscarUsuario();
        if (usuario == null) return;

        String verificaEmprestimoSql = "SELECT COUNT(*) FROM emprestimos WHERE usuario_id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(verificaEmprestimoSql)) {

            stmt.setInt(1, usuario.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Não é possível remover o usuário! Ele está sendo utilizado em um ou mais empréstimos.");
                }
            }

            String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                deleteStmt.setInt(1, usuario.getId());
                deleteStmt.executeUpdate();
                System.out.println("Usuário removido com sucesso!");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao remover usuário: " + e.getMessage());
        }
    }
}