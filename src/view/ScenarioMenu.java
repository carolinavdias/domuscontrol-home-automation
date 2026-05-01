package view;

import controller.DomusController;
import exceptions.UserNotFoundException;
import model.automations.Scenario;
import model.users.User;

import java.util.List;
import java.util.Scanner;

// menu para gerir e executar cenários do utilizador
public class ScenarioMenu {

    private DomusController controller;
    private Scanner scanner;

    public ScenarioMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              Cenários                │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Listar os meus cenários          │");
            System.out.println("│  2. Criar novo cenário               │");
            System.out.println("│  3. Executar cenário                 │");
            System.out.println("│  0. Voltar                           │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1": listarCenarios();  break;
                case "2": criarCenario();    break;
                case "3": executarCenario(); break;
                case "0": voltar = true;     break;
                default:  System.out.println("⚠ Opção inválida.");
            }
        }
    }

    // ==========================================================
    // LISTA CENÁRIOS

    private void listarCenarios() {
        try {
            User u = controller.getUtilizadorAtual();
            List<Scenario> cenarios = u.getCenarios();

            if (cenarios.isEmpty()) {
                System.out.println("\n(Ainda não tens cenários criados.)");
                return;
            }

            System.out.println("\n── Os teus cenários ──");
            for (int i = 0; i < cenarios.size(); i++) {
                Scenario c = cenarios.get(i);
                System.out.printf("  %d. [%s] %s | %s | Ações: %d%n",
                        i + 1, c.getId(), c.getNome(),
                        c.getDescricao(), c.getAcoes().size());
            }
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro de sessão: " + e.getMessage());
        }
    }

    // ==========================================================
    // CRIA CENÁRIO

    private void criarCenario() {
        System.out.println("\n── Criar Novo Cenário ──");

        System.out.print("ID do cenário (ex: sc5): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome do cenário: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        Scenario cenario = new Scenario(id, nome, descricao);

        System.out.println("\nAdiciona as ações (dispositivos a ligar/desligar).");
        System.out.println("Escreve 'fim' no ID do dispositivo para terminar.");

        while (true) {
            System.out.print("ID do dispositivo (ou 'fim'): ");
            String idDisp = scanner.nextLine().trim();
            if (idDisp.equalsIgnoreCase("fim")) break;

            System.out.print("Ação (ligar/desligar): ");
            String acao = scanner.nextLine().trim().toLowerCase();

            try {
                cenario.adicionarAcao(idDisp, acao);
                System.out.println("  ✓ Ação adicionada.");
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ " + e.getMessage());
            }
        }

        try {
            controller.adicionarCenarioAoUtilizador(cenario);
            System.out.println("✓ Cenário '" + nome + "' criado e guardado com sucesso!");
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro ao guardar cenário: " + e.getMessage());
        }
    }

    // ==========================================================
    // EXECUTA CENÁRIO

    private void executarCenario() {
        System.out.println("\n── Executar Cenário ──");

        try {
            User u = controller.getUtilizadorAtual();
            List<Scenario> cenarios = u.getCenarios();

            if (cenarios.isEmpty()) {
                System.out.println("(Não tens cenários disponíveis.)");
                return;
            }

            System.out.println("Cenários disponíveis:");
            for (int i = 0; i < cenarios.size(); i++) {
                System.out.printf("  %d. [%s] %s — %s%n",
                        i + 1,
                        cenarios.get(i).getId(),
                        cenarios.get(i).getNome(),
                        cenarios.get(i).getDescricao());
            }

            System.out.print("Número do cenário a executar: ");
            int escolha;
            try {
                escolha = Integer.parseInt(scanner.nextLine().trim()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("✗ Opção inválida.");
                return;
            }

            if (escolha < 0 || escolha >= cenarios.size()) {
                System.out.println("✗ Cenário inexistente.");
                return;
            }

            String idCenario = cenarios.get(escolha).getId();
            String nomeCenario = cenarios.get(escolha).getNome();

            controller.executarCenario(idCenario);
            System.out.println("✓ Cenário '" + nomeCenario + "' executado com sucesso!");

        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro de sessão: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }
}