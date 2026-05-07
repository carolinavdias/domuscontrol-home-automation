package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import model.automations.Automation;

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
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│             Automações               │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Ver automações de uma casa       │");
            System.out.println("│  2. Criar nova automação             │");
            System.out.println("│  3. Testar automação (sensor)        │");
            System.out.println("│  0. Voltar                           │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": verAutomacoes();   break;
                case "2": criarAutomacao();  break;
                case "3": testarAutomacao(); break;
                case "0": sair = true;       break;
                default: System.out.println("⚠ Opção inválida.");
            }
        }
    }

    //==========================================================

    private void verAutomacoes() {
        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();
        try {
            java.util.List<Automation> lista =
                controller.getCasa(idCasa).getAutomacoes();
            if (lista.isEmpty()) {
                System.out.println("  (sem automações)");
            } else {
                lista.forEach(a -> System.out.println("  " + a));
            }
        } catch (exceptions.HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    private void criarAutomacao() {
        System.out.println("\n── Nova Automação ──");

        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("ID da automação (ex: auto3): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição da condição: ");
        String descricao = scanner.nextLine().trim();

        System.out.print("ID do sensor: ");
        String idSensor = scanner.nextLine().trim();

        System.out.print("Limiar (valor que dispara a automação): ");
        double limiar;
        try {
            limiar = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Valor inválido.");
            return;
        }

        System.out.println("Ação a executar:");
        System.out.println("  1. Ligar dispositivos");
        System.out.println("  2. Desligar dispositivos");
        System.out.print("Opção: ");
        String opcaoAcao = scanner.nextLine().trim();
        String acao = opcaoAcao.equals("1") ? "ligar" : "desligar";

        Automation auto = new Automation(id, nome, descricao, idSensor, limiar, acao);

        System.out.print("IDs dos dispositivos afetados (separados por vírgula): ");
        String[] ids = scanner.nextLine().trim().split(",");
        for (String idDisp : ids) {
            auto.adicionarDispositivo(idDisp.trim());
        }

        try {
            controller.adicionarAutomacao(idCasa, auto);
            System.out.println("✓ Automação criada com sucesso!");
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    private void testarAutomacao() {
        System.out.println("\n── Testar Automação ──");
        System.out.println("  Atualiza o valor de um sensor e verifica se alguma automação dispara.");

        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("ID da divisão do sensor: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("ID do sensor: ");
        String idSensor = scanner.nextLine().trim();

        System.out.print("Novo valor do sensor: ");
        try {
            double valor = Double.parseDouble(scanner.nextLine().trim());
            controller.atualizarSensor(idCasa, idDivisao, idSensor, valor);
            System.out.println("✓ Sensor atualizado! Automações verificadas.");
        } catch (NumberFormatException e) {
            System.out.println("✗ Valor inválido.");
        } catch (Exception e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }
}