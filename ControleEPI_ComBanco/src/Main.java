import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EPIDao dao = new EPIDao();
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n===== MENU EPI =====");
            System.out.println("1 - Cadastrar novo EPI");
            System.out.println("2 - Listar todos os EPIs");
            System.out.println("3 - Buscar EPI por ID");
            System.out.println("4 - Atualizar EPI");
            System.out.println("5 - Deletar EPI");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Digite o nome do EPI: ");
                    String nome = sc.nextLine();
                    System.out.print("Digite a validade do EPI (AAAA-MM-DD): ");
                    String validade = sc.nextLine();
                    EPI novo = new EPI(nome, validade);
                    dao.inserirEPI(novo);
                    break;

                case 2:
                    System.out.println("\nLista de EPIs:");
                    for (EPI epi : dao.listarEPIs()) {
                        System.out.println("ID: " + epi.getId() +
                                " | Nome: " + epi.getNome() +
                                " | Validade: " + epi.getValidade());
                    }
                    break;

                case 3:
                    System.out.print("Digite o ID do EPI a buscar: ");
                    int idBusca = sc.nextInt();
                    EPI encontrado = dao.buscarEPIporId(idBusca);
                    if (encontrado != null) {
                        System.out.println("EPI encontrado:");
                        System.out.println("ID: " + encontrado.getId() +
                                " | Nome: " + encontrado.getNome() +
                                " | Validade: " + encontrado.getValidade());
                    } else {
                        System.out.println("Nenhum EPI encontrado com esse ID.");
                    }
                    break;

                case 4:
                    System.out.print("Digite o ID do EPI a atualizar: ");
                    int idAtualizar = sc.nextInt();
                    sc.nextLine();
                    EPI epiAtualizar = dao.buscarEPIporId(idAtualizar);
                    if (epiAtualizar != null) {
                        System.out.print("Digite o novo nome do EPI: ");
                        String novoNome = sc.nextLine();
                        System.out.print("Digite a nova validade do EPI (AAAA-MM-DD): ");
                        String novaValidade = sc.nextLine();
                        epiAtualizar.setNome(novoNome);
                        epiAtualizar.setValidade(novaValidade);
                        dao.atualizarEPI(epiAtualizar);
                    } else {
                        System.out.println("EPI não encontrado para atualização.");
                    }
                    break;

                case 5:
                    System.out.print("Digite o ID do EPI a deletar: ");
                    int idDeletar = sc.nextInt();
                    dao.deletarEPI(idDeletar);
                    break;

                case 0:
                    System.out.println("Saindo do programa...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }

        } while (opcao != 0);

        sc.close();
    }
}