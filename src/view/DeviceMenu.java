package view;

import controller.DomusController;
import exceptions.DeviceNotFoundException;
import exceptions.HouseNotFoundException;
import exceptions.RoomNotFoundException;
import model.devices.Device;
import model.house.House;
import model.house.Room;

import java.util.List;
import java.util.Scanner;

// menu para operar dispositivos: ligar, desligar e ver estado
public class DeviceMenu {

    private DomusController controller;
    private Scanner scanner;

    public DeviceMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│          Gerir Dispositivos          │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Ligar dispositivo                │");
            System.out.println("│  2. Desligar dispositivo             │");
            System.out.println("│  3. Ver estado de um dispositivo     │");
            System.out.println("│  4. Listar todos os dispositivos     │");
            System.out.println("│  0. Voltar                           │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1": operarDispositivo(true);  break;
                case "2": operarDispositivo(false); break;
                case "3": verEstado();              break;
                case "4": listarTodos();            break;
                case "0": voltar = true;            break;
                default:  System.out.println("⚠ Opção inválida.");
            }
        }
    }

    // ==========================================================
    // LIGAR / DESLIGAR

    private void operarDispositivo(boolean ligar) {
        String acao = ligar ? "Ligar" : "Desligar";
        System.out.println("\n── " + acao + " Dispositivo ──");

        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("ID do dispositivo: ");
        String idDispositivo = scanner.nextLine().trim();

        try {
            if (ligar) {
                controller.ligarDispositivo(idCasa, idDivisao, idDispositivo);
                System.out.println("✓ Dispositivo ligado.");
            } else {
                controller.desligarDispositivo(idCasa, idDivisao, idDispositivo);
                System.out.println("✓ Dispositivo desligado.");
            }
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        } catch (RoomNotFoundException e) {
            System.out.println("✗ Divisão não encontrada.");
        } catch (DeviceNotFoundException e) {
            System.out.println("✗ Dispositivo não encontrado.");
        }
    }

    // ==========================================================
    // Vê ESTADO

    private void verEstado() {
        System.out.println("\n── Ver Estado do Dispositivo ──");

        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("ID do dispositivo: ");
        String idDispositivo = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            Room r = h.getDivisao(idDivisao);
            if (r == null) { System.out.println("✗ Divisão não encontrada."); return; }

            Device d = r.getDispositivo(idDispositivo);
            if (d == null) { System.out.println("✗ Dispositivo não encontrado."); return; }

            System.out.println("\n── Estado ──");
            System.out.println("  " + d.toString());
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // LISTA TODOS

    private void listarTodos() {
        System.out.print("\nID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            System.out.println("\n── Dispositivos de '" + h.getNome() + "' ──");

            for (Room r : h.getDivisoes()) {
                System.out.println("  📁 " + r.getNome());
                List<Device> dispositivos = r.getDispositivos();
                if (dispositivos.isEmpty()) {
                    System.out.println("     (sem dispositivos)");
                } else {
                    for (Device d : dispositivos) {
                        System.out.printf("     [%s] %s %s | %s%n",
                                d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
                    }
                }
            }
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }
}