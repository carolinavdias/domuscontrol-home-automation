package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
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

        System.out.println("\nAgora adiciona as ações (dispositivos a ligar/desligar).");
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

        // adiciona o cenário ao utilizador atual
        try {
            // nota: quando o controller expor este método, usar controller.adicionarCenarioAoUtilizador()
            // por agora adicionamos diretamente via getUtilizadorAtual()
            // ATENÇÃO: getUtilizadorAtual() devolve cópia — este método não persiste ainda!
            System.out.println("✓ Cenário '" + nome + "' criado com " + cenario.getAcoes().size() + " ações.");
            System.out.println("⚠ Para persistir cenários, a tua colega precisa de expor adicionarCenarioAoUtilizador() no controller.");
        } catch (Exception e) {
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

            // mostra a lista
            for (int i = 0; i < cenarios.size(); i++) {
                System.out.printf("  %d. %s — %s%n",
                        i + 1, cenarios.get(i).getNome(), cenarios.get(i).getDescricao());
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

            System.out.print("ID da casa onde executar: ");
            String idCasa = scanner.nextLine().trim();

            System.out.print("ID da divisão: ");
            String idDivisao = scanner.nextLine().trim();

            Scenario c = cenarios.get(escolha);
            int sucessos = 0;
            int erros = 0;

            for (String[] acao : c.getAcoes()) {
                String idDisp = acao[0];
                String tipoAcao = acao[1];
                try {
                    if (tipoAcao.equals("ligar")) {
                        controller.ligarDispositivo(idCasa, idDivisao, idDisp);
                    } else {
                        controller.desligarDispositivo(idCasa, idDivisao, idDisp);
                    }
                    System.out.printf("  ✓ %s → %s%n", idDisp, tipoAcao);
                    sucessos++;
                } catch (Exception e) {
                    System.out.printf("  ✗ %s → erro (%s)%n", idDisp, e.getMessage());
                    erros++;
                }
            }

            System.out.printf("%nCenário '%s' executado: %d ações com sucesso, %d erros.%n",
                    c.getNome(), sucessos, erros);

        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro de sessão.");
        }
    }
}