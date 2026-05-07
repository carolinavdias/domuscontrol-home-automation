package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import exceptions.InvalidPermissionException;
import exceptions.RoomNotFoundException;
import exceptions.UserNotFoundException;
import model.devices.*;
import model.house.House;
import model.house.Room;
import utils.UIColors;

import java.util.List;
import java.util.Scanner;

// menu responsável por gerir casas e divisões
// só administradores podem criar casas, divisões e adicionar dispositivos
public class HouseMenu {

    private DomusController controller;
    private Scanner scanner;

    public HouseMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    //==========================================================

    public void mostrar() {
        boolean voltar = false;
        while (!voltar) {
            String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
            System.out.println();
            System.out.println(c + "  ┌──────────────────────────────────────┐" + r);
            System.out.println(c + "  │" + r + "        Gerir Casas e Divisões        " + c + "│" + r);
            System.out.println(c + "  ├──────────────────────────────────────┤" + r);
            System.out.println(c + "  │" + r + "  " + y + "1" + r + ". Listar as minhas casas           " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "2" + r + ". Criar nova casa                  " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "3" + r + ". Gerir divisões de uma casa       " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "4" + r + ". Adicionar dispositivo            " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "5" + r + ". Ver detalhes de uma casa         " + c + "│" + r);
            System.out.println(c + "  │" + r + "  " + y + "0" + r + ". Voltar                           " + c + "│" + r);
            System.out.println(c + "  └──────────────────────────────────────┘" + r);
            System.out.print("  Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1": listarCasas();          break;
                case "2": criarCasa();            break;
                case "3": menuDivisoes();         break;
                case "4": adicionarDispositivo(); break;
                case "5": verDetalhesCasa();      break;
                case "0": voltar = true;          break;
                default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
            }
        }
    }

    //==========================================================

    private void listarCasas() {
        try {
            List<House> casas = controller.getCasasDoUtilizadorAtual();
            if (casas.isEmpty()) {
                System.out.println("\n  " + UIColors.DIM + "(Ainda não tens casas associadas.)" + UIColors.RESET);
                return;
            }
            System.out.println("\n" + UIColors.BCYAN + "  ── As tuas casas ──" + UIColors.RESET);
            for (int i = 0; i < casas.size(); i++) {
                House h = casas.get(i);
                System.out.printf("  " + UIColors.BYELLOW + "%d" + UIColors.RESET + ". [%s] %s | %s | Divisões: %d%n",
                        i + 1, h.getId(), h.getNome(), h.getMorada(), h.getNumDivisoes());
            }
        } catch (UserNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void criarCasa() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Criar Nova Casa ──" + UIColors.RESET);

        System.out.print("  ID da casa (ex: c2): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome da casa: ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Morada: ");
        String morada = scanner.nextLine().trim();

        try {
            controller.criarCasa(new House(id, nome, morada));
            System.out.println("  " + UIColors.BGREEN + "✓ Casa '" + nome + "' criada com sucesso!" + UIColors.RESET);
        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void menuDivisoes() {
        System.out.print("\n  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            boolean voltar = false;

            while (!voltar) {
                String c = UIColors.BCYAN, y = UIColors.BYELLOW, r = UIColors.RESET;
                System.out.println("\n" + c + "  ── Divisões de '" + h.getNome() + "' ──" + r);
                System.out.println("  " + y + "1" + r + ". Listar divisões");
                System.out.println("  " + y + "2" + r + ". Criar nova divisão");
                System.out.println("  " + y + "3" + r + ". Ver dispositivos de uma divisão");
                System.out.println("  " + y + "0" + r + ". Voltar");
                System.out.print("  Opção: ");

                switch (scanner.nextLine().trim()) {
                    case "1": listarDivisoes(idCasa);        break;
                    case "2": criarDivisao(idCasa);          break;
                    case "3": verDispositivosDivisao(idCasa);break;
                    case "0": voltar = true;                 break;
                    default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
                }
            }
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void listarDivisoes(String idCasa) {
        try {
            House h = controller.getCasa(idCasa);
            List<Room> divisoes = h.getDivisoes();
            if (divisoes.isEmpty()) {
                System.out.println("  " + UIColors.DIM + "(Esta casa ainda não tem divisões.)" + UIColors.RESET);
                return;
            }
            System.out.println("\n" + UIColors.BCYAN + "  ── Divisões ──" + UIColors.RESET);
            for (Room r : divisoes)
                System.out.printf("  " + UIColors.DIM + "[%s]" + UIColors.RESET + " %s | Dispositivos: %d | Consumo: %.1f Wh%n",
                        r.getId(), r.getNome(), r.getNumDispositivos(), r.getConsumoTotal());
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void criarDivisao(String idCasa) {
        System.out.println("\n" + UIColors.BCYAN + "  ── Criar Nova Divisão ──" + UIColors.RESET);

        System.out.print("  ID da divisão (ex: r5): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome da divisão (ex: Sala, Quarto): ");
        String nome = scanner.nextLine().trim();

        try {
            controller.adicionarDivisao(idCasa, new Room(id, nome));
            System.out.println("  " + UIColors.BGREEN + "✓ Divisão '" + nome + "' criada com sucesso!" + UIColors.RESET);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        } catch (InvalidPermissionException e) {
            System.out.println("  " + UIColors.BRED + "✗ Não tens permissão de administrador para esta casa." + UIColors.RESET);
        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void verDispositivosDivisao(String idCasa) {
        System.out.print("  ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            Room r = h.getDivisao(idDivisao);
            if (r == null) { System.out.println("  " + UIColors.BRED + "✗ Divisão não encontrada." + UIColors.RESET); return; }

            List<Device> dispositivos = r.getDispositivos();
            if (dispositivos.isEmpty()) {
                System.out.println("  " + UIColors.DIM + "(Esta divisão ainda não tem dispositivos.)" + UIColors.RESET);
                return;
            }
            System.out.println("\n" + UIColors.BCYAN + "  ── Dispositivos de '" + r.getNome() + "' ──" + UIColors.RESET);
            for (Device d : dispositivos)
                System.out.printf("  " + UIColors.DIM + "[%s]" + UIColors.RESET + " %s %s | %s%n",
                        d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private void adicionarDispositivo() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Adicionar Dispositivo ──" + UIColors.RESET);

        System.out.print("  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("  ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        String y = UIColors.BYELLOW, r = UIColors.RESET;
        System.out.println("\n  Tipo de dispositivo:");
        System.out.println("  " + y + "1" + r + ". Lâmpada");
        System.out.println("  " + y + "2" + r + ". Lâmpada de cor");
        System.out.println("  " + y + "3" + r + ". Coluna");
        System.out.println("  " + y + "4" + r + ". Cortina");
        System.out.println("  " + y + "5" + r + ". Portão de garagem");
        System.out.println("  " + y + "6" + r + ". Relé");
        System.out.println("  " + y + "7" + r + ". Tomada");
        System.out.print("  Tipo: ");
        String tipo = scanner.nextLine().trim();

        System.out.print("  ID do dispositivo (ex: d12): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Marca: ");
        String marca = scanner.nextLine().trim();

        System.out.print("  Modelo: ");
        String modelo = scanner.nextLine().trim();

        System.out.print("  Consumo (Wh): ");
        double consumo;
        try {
            consumo = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  " + UIColors.BRED + "✗ Valor de consumo inválido." + UIColors.RESET);
            return;
        }

        Device d = criarDispositivo(tipo, id, marca, modelo, consumo);
        if (d == null) { System.out.println("  " + UIColors.BRED + "✗ Tipo inválido." + UIColors.RESET); return; }

        try {
            controller.adicionarDispositivo(idCasa, idDivisao, d);
            System.out.println("  " + UIColors.BGREEN + "✓ Dispositivo adicionado com sucesso!" + UIColors.RESET);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        } catch (RoomNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Divisão não encontrada." + UIColors.RESET);
        } catch (InvalidPermissionException e) {
            System.out.println("  " + UIColors.BRED + "✗ Não tens permissão de administrador para esta casa." + UIColors.RESET);
        } catch (UserNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro de sessão: " + e.getMessage() + UIColors.RESET);
        }
    }

    private Device criarDispositivo(String tipo, String id, String marca, String modelo, double consumo) {
        switch (tipo) {
            case "1":
                System.out.print("  Brilho (0-100): ");
                return new Lamp(id, marca, modelo, consumo, lerInteiro(0, 100));
            case "2":
                System.out.print("  Brilho (0-100): ");
                int brilho = lerInteiro(0, 100);
                System.out.print("  Temperatura de cor (2700-4000 K): ");
                return new ColorLamp(id, marca, modelo, consumo, brilho, lerInteiro(2700, 4000));
            case "3":
                System.out.print("  Volume (0-100): ");
                return new Speaker(id, marca, modelo, consumo, lerInteiro(0, 100));
            case "4": return new Curtain(id, marca, modelo, consumo);
            case "5": return new GarageGate(id, marca, modelo, consumo);
            case "6": return new Relay(id, marca, modelo, consumo);
            case "7": return new Socket(id, marca, modelo, consumo);
            default:  return null;
        }
    }

    private void verDetalhesCasa() {
        System.out.print("\n  ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            System.out.println("\n" + UIColors.BCYAN + "  ── Detalhes de '" + h.getNome() + "' ──" + UIColors.RESET);
            System.out.println("  ID:      " + UIColors.DIM + h.getId() + UIColors.RESET);
            System.out.println("  Nome:    " + h.getNome());
            System.out.println("  Morada:  " + h.getMorada());
            System.out.printf( "  Consumo total: %.1f Wh%n", h.getConsumoTotal());
            System.out.println("  Divisões:");
            for (Room r : h.getDivisoes()) {
                System.out.printf("    " + UIColors.DIM + "[%s]" + UIColors.RESET + " %s | %d dispositivos | %.1f Wh%n",
                        r.getId(), r.getNome(), r.getNumDispositivos(), r.getConsumoTotal());
                for (Device d : r.getDispositivos())
                    System.out.printf("       → " + UIColors.DIM + "[%s]" + UIColors.RESET + " %s %s | %s%n",
                            d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
            }
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
        }
    }

    private int lerInteiro(int min, int max) {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.printf("  " + UIColors.BYELLOW + "⚠ Valor entre %d e %d: " + UIColors.RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.print("  " + UIColors.BYELLOW + "⚠ Inválido. Tenta novamente: " + UIColors.RESET);
            }
        }
    }
}