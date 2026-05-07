package view;

import controller.DomusController;
import exceptions.UserNotFoundException;
import model.automations.Scenario;
import model.users.User;
import utils.UIColors;

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
            String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
            System.out.println();
            System.out.println(c + "  ┌──────────────────────────────────────┐" + r);
            System.out.println(c + "  │" + r + "              Cenários                " + c + "│" + r);
            System.out.println(c + "  ├──────────────────────────────────────┤" + r);
            System.out.println(c + "  │" + r + "  " + y + "1" + r + ". Listar os meus cenários          " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "2" + r + ". Criar novo cenário               " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "3" + r + ". Executar cenário                 " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "0" + r + ". Voltar                           " + c + "│" + r);
            System.out.println(c + "  └──────────────────────────────────────┘" + r);
            System.out.print("  Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": listarCenarios();  break;
                case "2": criarCenario();    break;
                case "3": executarCenario(); break;
                case "0": voltar = true;     break;
                default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
            }
        }
    }

    //==========================================================

    private void listarCenarios() {
        try {
            User u = controller.getUtilizadorAtual();
            List<Scenario> cenarios = u.getCenarios();
            if (cenarios.isEmpty()) {
                System.out.println("\n  " + UIColors.DIM + "(Ainda não tens cenários criados.)" + UIColors.RESET);
                return;
            }
            System.out.println("\n" + UIColors.BCYAN + "  ── Os teus cenários ──" + UIColors.RESET);
            for (int i = 0; i < cenarios.size(); i++) {
                Scenario c = cenarios.get(i);
                System.out.printf("  " + UIColors.BYELLOW + "%d" + UIColors.RESET + ". [%s] %s | %s | Ações: %d%n",
                        i + 1, c.getId(), c.getNome(), c.getDescricao(), c.getAcoes().size());
            }
        } catch (UserNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro de sessão: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void criarCenario() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Criar Novo Cenário ──" + UIColors.RESET);

        System.out.print("  ID do cenário (ex: sc5): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome do cenário: ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Descrição: ");
        String descricao = scanner.nextLine().trim();

        Scenario cenario = new Scenario(id, nome, descricao);

        System.out.println("\n  Adiciona as ações. Escreve 'fim' no ID para terminar.");
        while (true) {
            System.out.print("  ID do dispositivo (ou 'fim'): ");
            String idDisp = scanner.nextLine().trim();
            if (idDisp.equalsIgnoreCase("fim")) break;

            System.out.print("  Ação (ligar/desligar): ");
            String acao = scanner.nextLine().trim().toLowerCase();

            try {
                cenario.adicionarAcao(idDisp, acao);
                System.out.println("  " + UIColors.BGREEN + "✓ Ação adicionada." + UIColors.RESET);
            } catch (IllegalArgumentException e) {
                System.out.println("  " + UIColors.BRED + "✗ " + e.getMessage() + UIColors.RESET);
            }
        }

        try {
            controller.adicionarCenarioAoUtilizador(cenario);
            System.out.println("  " + UIColors.BGREEN + "✓ Cenário '" + nome + "' criado com sucesso!" + UIColors.RESET);
        } catch (UserNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro ao guardar cenário: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void executarCenario() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Executar Cenário ──" + UIColors.RESET);

        try {
            User u = controller.getUtilizadorAtual();
            List<Scenario> cenarios = u.getCenarios();

            if (cenarios.isEmpty()) {
                System.out.println("  " + UIColors.DIM + "(Não tens cenários disponíveis.)" + UIColors.RESET);
                return;
            }

            System.out.println("  Cenários disponíveis:");
            for (int i = 0; i < cenarios.size(); i++)
                System.out.printf("  " + UIColors.BYELLOW + "%d" + UIColors.RESET + ". [%s] %s — %s%n",
                        i + 1, cenarios.get(i).getId(),
                        cenarios.get(i).getNome(), cenarios.get(i).getDescricao());

            System.out.print("  Número do cenário a executar: ");
            int escolha;
            try {
                escolha = Integer.parseInt(scanner.nextLine().trim()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("  " + UIColors.BRED + "✗ Opção inválida." + UIColors.RESET);
                return;
            }

            if (escolha < 0 || escolha >= cenarios.size()) {
                System.out.println("  " + UIColors.BRED + "✗ Cenário inexistente." + UIColors.RESET);
                return;
            }

            String idCenario  = cenarios.get(escolha).getId();
            String nomeCenario = cenarios.get(escolha).getNome();
            controller.executarCenario(idCenario);
            System.out.println("  " + UIColors.BGREEN + "✓ Cenário '" + nomeCenario + "' executado com sucesso!" + UIColors.RESET);

        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.out.println("  " + UIColors.BRED + "✗ " + e.getMessage() + UIColors.RESET);
        }
    }
}