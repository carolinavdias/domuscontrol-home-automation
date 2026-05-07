package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import model.automations.Scheduling;
import model.house.House;
import utils.UIColors;

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
            String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
            System.out.println();
            System.out.println(c + "  ┌──────────────────────────────────────┐" + r);
            System.out.println(c + "  │" + r + "           Escalonamentos             " + c + "│" + r);
            System.out.println(c + "  ├──────────────────────────────────────┤" + r);
            System.out.println(c + "  │" + r + "  " + y + "1" + r + ". Listar escalonamentos de uma casa" + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "2" + r + ". Criar novo escalonamento         " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "3" + r + ". Verificar escalonamentos agora   " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "0" + r + ". Voltar                           " + c + "│" + r);
            System.out.println(c + "  └──────────────────────────────────────┘" + r);
            System.out.print("  Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": listarEscalonamentos();    break;
                case "2": criarEscalonamento();      break;
                case "3": verificarEscalonamentos(); break;
                case "0": voltar = true;             break;
                default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
            }
        }
    }

    //==========================================================

    private void listarEscalonamentos() {
        System.out.print("\n  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            List<Scheduling> lista = h.getEscalonamentos();
            if (lista.isEmpty()) {
                System.out.println("  " + UIColors.DIM + "(Esta casa ainda não tem escalonamentos.)" + UIColors.RESET);
                return;
            }
            System.out.println("\n" + UIColors.BCYAN + "  ── Escalonamentos de '" + h.getNome() + "' ──" + UIColors.RESET);
            for (int i = 0; i < lista.size(); i++) {
                Scheduling s = lista.get(i);
                System.out.printf("  " + UIColors.BYELLOW + "%d" + UIColors.RESET
                        + ". [%s] %s | Hora: %s | %s | %s | Dispositivos: %d%n",
                        i + 1, s.getId(), s.getNome(), s.getHoraExecucao(),
                        s.isLigar() ? "LIGAR" : "DESLIGAR",
                        s.isRepeteDaily() ? "diário" : "único",
                        s.getIdsDispositivos().size());
            }
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void criarEscalonamento() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Criar Novo Escalonamento ──" + UIColors.RESET);

        System.out.print("  ID do escalonamento (ex: esc5): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Hora de execução (HH:MM): ");
        LocalTime horaExecucao = lerHora();
        if (horaExecucao == null) return;

        System.out.print("  Hora de fim (HH:MM) ou Enter para ignorar: ");
        String horaFimStr = scanner.nextLine().trim();
        LocalTime horaFim = null;
        if (!horaFimStr.isEmpty()) {
            try {
                horaFim = LocalTime.parse(horaFimStr);
            } catch (DateTimeParseException e) {
                System.out.println("  " + UIColors.BYELLOW + "⚠ Hora inválida, a ignorar hora de fim." + UIColors.RESET);
            }
        }

        System.out.print("  Repete diariamente? (s/n): ");
        boolean repete = scanner.nextLine().trim().equalsIgnoreCase("s");

        System.out.print("  Ação (ligar/desligar): ");
        boolean ligar = scanner.nextLine().trim().toLowerCase().equals("ligar");

        Scheduling s = new Scheduling(id, nome, horaExecucao, horaFim, repete, ligar);

        System.out.println("  Adiciona os dispositivos. Escreve 'fim' para terminar:");
        while (true) {
            System.out.print("  ID do dispositivo (ou 'fim'): ");
            String idDisp = scanner.nextLine().trim();
            if (idDisp.equalsIgnoreCase("fim")) break;
            try {
                s.adicionarDispositivo(idDisp);
                System.out.println("  " + UIColors.BGREEN + "✓ Adicionado." + UIColors.RESET);
            } catch (IllegalArgumentException e) {
                System.out.println("  " + UIColors.BRED + "✗ " + e.getMessage() + UIColors.RESET);
            }
        }

        System.out.print("  ID da casa onde guardar: ");
        String idCasa = scanner.nextLine().trim();

        try {
            controller.adicionarEscalonamento(idCasa, s);
            System.out.println("  " + UIColors.BGREEN + "✓ Escalonamento '" + nome + "' criado com sucesso!" + UIColors.RESET);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void verificarEscalonamentos() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Verificar Escalonamentos ──" + UIColors.RESET);
        controller.verificarEscalonamentos();
        System.out.println("  " + UIColors.BGREEN + "✓ Escalonamentos verificados para a hora atual." + UIColors.RESET);
    }

    private LocalTime lerHora() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.print("  " + UIColors.BYELLOW + "⚠ Formato inválido. Usa HH:MM (ex: 07:30): " + UIColors.RESET);
            }
        }
    }
}