import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EPIDao dao = new EPIDao();
        Scanner sc = new Scanner(System.in);

        System.out.println("Digite o nome do EPI:");
        String nome = sc.nextLine();

        System.out.println("Digite a validade do EPI (AAAA-MM-DD):");
        String validade = sc.nextLine();

        EPI novo = new EPI(nome, validade);
        dao.inserirEPI(novo);

        System.out.println("\nEPIs cadastrados:");
        for (EPI epi : dao.listarEPIs()) {
            System.out.println("ID: " + epi.getId() + " | Nome: " + epi.getNome() + " | Validade: " + epi.getValidade());
        }
        sc.close();
    }
}