package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import model.automations.Automation;
import utils.UIColors;

import java.util.Scanner;

// menu para gerir e testar as automações de uma casa
public class AutomationMenu {

    private DomusController controller;
    private Scanner scanner;

    public AutomationMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean sair = false;
        while (!sair) {
            String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
            System.out.println();
            System.out.println(c + "  ┌──────────────────────────────────────┐" + r);
            System.out.println(c + "  │" + r + "             Automações               " + c + "│" + r);
            System.out.println(c + "  ├──────────────────────────────────────┤" + r);
            System.out.println(c + "  │" + r + "  " + y + "1" + r + ". Ver automações de uma casa       " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "2" + r + ". Criar nova automação             " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "3" + r + ". Testar automação (sensor)        " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "0" + r + ". Voltar                           " + c + "│" + r);
            System.out.println(c + "  └──────────────────────────────────────┘" + r);
            System.out.print("  Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": verAutomacoes();   break;
                case "2": criarAutomacao();  break;
                case "3": testarAutomacao(); break;
                case "0": sair = true;       break;
                default: System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
            }
        }
    }

    //==========================================================

    private void verAutomacoes() {
        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();
        try {
            java.util.List<Automation> lista = controller.getCasa(idCasa).getAutomacoes();
            if (lista.isEmpty()) {
                System.out.println("  " + UIColors.DIM + "(sem automações)" + UIColors.RESET);
            } else {
                lista.forEach(a -> System.out.println("  " + a));
            }
        } catch (exceptions.HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void criarAutomacao() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Nova Automação ──" + UIColors.RESET);

        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("  ID da automação (ex: auto3): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Descrição da condição: ");
        String descricao = scanner.nextLine().trim();

        System.out.print("  ID do sensor: ");
        String idSensor = scanner.nextLine().trim();

        System.out.print("  Limiar (valor que dispara a automação): ");
        double limiar;
        try {
            limiar = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  " + UIColors.BRED + "✗ Valor inválido." + UIColors.RESET);
            return;
        }

        System.out.println("  Ação a executar:");
        System.out.println("  " + UIColors.BYELLOW + "1" + UIColors.RESET + ". Ligar dispositivos");
        System.out.println("  " + UIColors.BYELLOW + "2" + UIColors.RESET + ". Desligar dispositivos");
        System.out.print("  Opção: ");
        String opcaoAcao = scanner.nextLine().trim();
        String acao = opcaoAcao.equals("1") ? "ligar" : "desligar";

        Automation auto = new Automation(id, nome, descricao, idSensor, limiar, acao);

        System.out.print("  IDs dos dispositivos afetados (separados por vírgula): ");
        String[] ids = scanner.nextLine().trim().split(",");
        for (String idDisp : ids)
            auto.adicionarDispositivo(idDisp.trim());

        try {
            controller.adicionarAutomacao(idCasa, auto);
            System.out.println("  " + UIColors.BGREEN + "✓ Automação criada com sucesso!" + UIColors.RESET);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void testarAutomacao() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Testar Automação ──" + UIColors.RESET);
        System.out.println("  Atualiza o valor de um sensor e verifica se alguma automação dispara.");

        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("  ID da divisão do sensor: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("  ID do sensor: ");
        String idSensor = scanner.nextLine().trim();

        System.out.print("  Novo valor do sensor: ");
        try {
            double valor = Double.parseDouble(scanner.nextLine().trim());
            controller.atualizarSensor(idCasa, idDivisao, idSensor, valor);
            System.out.println("  " + UIColors.BGREEN + "✓ Sensor atualizado! Automações verificadas." + UIColors.RESET);
        } catch (NumberFormatException e) {
            System.out.println("  " + UIColors.BRED + "✗ Valor inválido." + UIColors.RESET);
        } catch (Exception e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro: " + e.getMessage() + UIColors.RESET);
        }
    }
}