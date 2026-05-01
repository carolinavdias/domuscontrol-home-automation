package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import model.automations.Scheduling;
import model.house.House;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

// menu para criar e gerir escalonamentos de uma casa
public class SchedulingMenu {

    private DomusController controller;
    private Scanner scanner;

    public SchedulingMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│           Escalonamentos             │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Listar escalonamentos de uma casa│");
            System.out.println("│  2. Criar novo escalonamento         │");
            System.out.println("│  3. Verificar escalonamentos agora   │");
            System.out.println("│  0. Voltar                           │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1": listarEscalonamentos();    break;
                case "2": criarEscalonamento();      break;
                case "3": verificarEscalonamentos(); break;
                case "0": voltar = true;             break;
                default:  System.out.println("⚠ Opção inválida.");
            }
        }
    }

    // ==========================================================
    // LISTA ESCALONAMENTOS

    private void listarEscalonamentos() {
        System.out.print("\nID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            List<Scheduling> lista = h.getEscalonamentos();

            if (lista.isEmpty()) {
                System.out.println("(Esta casa ainda não tem escalonamentos.)");
                return;
            }

            System.out.println("\n── Escalonamentos de '" + h.getNome() + "' ──");
            for (int i = 0; i < lista.size(); i++) {
                Scheduling s = lista.get(i);
                System.out.printf("  %d. [%s] %s | Hora: %s | %s | %s | Dispositivos: %d%n",
                        i + 1,
                        s.getId(),
                        s.getNome(),
                        s.getHoraExecucao(),
                        s.isLigar() ? "LIGAR" : "DESLIGAR",
                        s.isRepeteDaily() ? "diário" : "único",
                        s.getIdsDispositivos().size());
            }
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // CRIA ESCALONAMENTO

    private void criarEscalonamento() {
        System.out.println("\n── Criar Novo Escalonamento ──");

        System.out.print("ID do escalonamento (ex: sc5): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Hora de execução (HH:MM): ");
        LocalTime horaExecucao = lerHora();
        if (horaExecucao == null) return;

        System.out.print("Hora de fim (HH:MM) ou Enter para ignorar: ");
        String horaFimStr = scanner.nextLine().trim();
        LocalTime horaFim = null;
        if (!horaFimStr.isEmpty()) {
            try {
                horaFim = LocalTime.parse(horaFimStr);
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Hora inválida, a ignorar hora de fim.");
            }
        }

        System.out.print("Repete diariamente? (s/n): ");
        boolean repete = scanner.nextLine().trim().equalsIgnoreCase("s");

        System.out.print("Ação (ligar/desligar): ");
        String acao = scanner.nextLine().trim().toLowerCase();
        boolean ligar = acao.equals("ligar");

        Scheduling s = new Scheduling(id, nome, horaExecucao, horaFim, repete, ligar);

        System.out.println("Adiciona os dispositivos afetados (escreve 'fim' para terminar):");
        while (true) {
            System.out.print("ID do dispositivo (ou 'fim'): ");
            String idDisp = scanner.nextLine().trim();
            if (idDisp.equalsIgnoreCase("fim")) break;
            try {
                s.adicionarDispositivo(idDisp);
                System.out.println("  ✓ Adicionado.");
            } catch (IllegalArgumentException e) {
                System.out.println("  ✗ " + e.getMessage());
            }
        }

        System.out.print("ID da casa onde guardar este escalonamento: ");
        String idCasa = scanner.nextLine().trim();

        try {
            controller.adicionarEscalonamento(idCasa, s);
            System.out.println("✓ Escalonamento '" + nome + "' criado e guardado com sucesso!");
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // VERIFICA ESCALONAMENTOS AGORA

    private void verificarEscalonamentos() {
        System.out.println("\n── Verificar Escalonamentos ──");
        controller.verificarEscalonamentos();
        System.out.println("✓ Escalonamentos verificados para a hora atual de simulação.");
    }

    // ==========================================================
    // UTILITÁRIO: lê hora no formato HH:MM

    private LocalTime lerHora() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.print("⚠ Formato inválido. Usa HH:MM (ex: 07:30): ");
            }
        }
    }
}