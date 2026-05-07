package view;

import controller.DomusController;
import exceptions.DeviceNotFoundException;
import exceptions.HouseNotFoundException;
import exceptions.RoomNotFoundException;
import model.devices.Device;
import model.house.House;
import model.house.Room;
import utils.UIColors;

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
            String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
            System.out.println();
            System.out.println(c + "  ┌──────────────────────────────────────┐" + r);
            System.out.println(c + "  │" + r + "          Gerir Dispositivos          " + c + "│" + r);
            System.out.println(c + "  ├──────────────────────────────────────┤" + r);
            System.out.println(c + "  │" + r + "  " + y + "1" + r + ". Ligar dispositivo                " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "2" + r + ". Desligar dispositivo             " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "3" + r + ". Ver estado de um dispositivo     " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "4" + r + ". Listar todos os dispositivos     " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "0" + r + ". Voltar                           " + c + "│" + r);
            System.out.println(c + "  └──────────────────────────────────────┘" + r);
            System.out.print("  Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": operarDispositivo(true);  break;
                case "2": operarDispositivo(false); break;
                case "3": verEstado();              break;
                case "4": listarTodos();            break;
                case "0": voltar = true;            break;
                default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
            }
        }
    }

    //==========================================================

    private void operarDispositivo(boolean ligar) {
        String acao = ligar ? "Ligar" : "Desligar";
        System.out.println("\n" + UIColors.BCYAN + "  ── " + acao + " Dispositivo ──" + UIColors.RESET);

        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("  ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("  ID do dispositivo: ");
        String idDispositivo = scanner.nextLine().trim();

        try {
            if (ligar) {
                controller.ligarDispositivo(idCasa, idDivisao, idDispositivo);
                System.out.println("  " + UIColors.BGREEN + "✓ Dispositivo ligado." + UIColors.RESET);
            } else {
                controller.desligarDispositivo(idCasa, idDivisao, idDispositivo);
                System.out.println("  " + UIColors.BGREEN + "✓ Dispositivo desligado." + UIColors.RESET);
            }
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        } catch (RoomNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Divisão não encontrada." + UIColors.RESET);
        } catch (DeviceNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Dispositivo não encontrado." + UIColors.RESET);
        }
    }

    private void verEstado() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Ver Estado do Dispositivo ──" + UIColors.RESET);

        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("  ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.print("  ID do dispositivo: ");
        String idDispositivo = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            Room r = h.getDivisao(idDivisao);
            if (r == null) { System.out.println("  " + UIColors.BRED + "✗ Divisão não encontrada." + UIColors.RESET); return; }

            Device d = r.getDispositivo(idDispositivo);
            if (d == null) { System.out.println("  " + UIColors.BRED + "✗ Dispositivo não encontrado." + UIColors.RESET); return; }

            System.out.println("\n" + UIColors.BCYAN + "  ── Estado ──" + UIColors.RESET);
            System.out.println("  " + d.toString());
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void listarTodos() {
        System.out.print("\n  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            System.out.println("\n" + UIColors.BCYAN + "  ── Dispositivos de '" + h.getNome() + "' ──" + UIColors.RESET);

            for (Room r : h.getDivisoes()) {
                System.out.println("  📁 " + UIColors.BOLD + r.getNome() + UIColors.RESET);
                List<Device> dispositivos = r.getDispositivos();
                if (dispositivos.isEmpty()) {
                    System.out.println("     " + UIColors.DIM + "(sem dispositivos)" + UIColors.RESET);
                } else {
                    for (Device d : dispositivos)
                        System.out.printf("     " + UIColors.DIM + "[%s]" + UIColors.RESET + " %s %s | %s%n",
                                d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
                }
            }
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }
}