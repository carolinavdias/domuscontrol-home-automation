package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import exceptions.InvalidPermissionException;
import exceptions.RoomNotFoundException;
import exceptions.UserNotFoundException;
import model.devices.*;
import model.house.House;
import model.house.Room;

import java.util.List;
import java.util.Scanner;

// menu responsável por gerir casas, divisões e dispositivos
// só administradores podem criar casas, divisões e adicionar dispositivos
public class HouseMenu {

    private DomusController controller;
    private Scanner scanner;

    public HouseMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    // ==========================================================
    // MENU PRINCIPAL DE CASAS

    public void mostrar() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│        Gerir Casas e Divisões        │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Listar as minhas casas           │");
            System.out.println("│  2. Criar nova casa                  │");
            System.out.println("│  3. Gerir divisões de uma casa       │");
            System.out.println("│  4. Adicionar dispositivo            │");
            System.out.println("│  5. Ver detalhes de uma casa         │");
            System.out.println("│  0. Voltar                           │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1": listarCasas();         break;
                case "2": criarCasa();           break;
                case "3": menuDivisoes();        break;
                case "4": adicionarDispositivo();break;
                case "5": verDetalhesCasa();     break;
                case "0": voltar = true;         break;
                default:  System.out.println("⚠ Opção inválida.");
            }
        }
    }

    // ==========================================================
    // LISTA CASAS

    private void listarCasas() {
        try {
            List<House> casas = controller.getCasasDoUtilizadorAtual();
            if (casas.isEmpty()) {
                System.out.println("\n(Ainda não tens casas associadas.)");
                return;
            }
            System.out.println("\n── As tuas casas ──");
            for (int i = 0; i < casas.size(); i++) {
                House h = casas.get(i);
                System.out.printf("  %d. [%s] %s | %s | Divisões: %d%n",
                        i + 1, h.getId(), h.getNome(), h.getMorada(), h.getNumDivisoes());
            }
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    // ==========================================================
    // CRIA CASA

    private void criarCasa() {
        System.out.println("\n── Criar Nova Casa ──");

        System.out.print("ID da casa (ex: c2): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome da casa: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Morada: ");
        String morada = scanner.nextLine().trim();

        try {
            House h = new House(id, nome, morada);
            controller.criarCasa(h);
            System.out.println("✓ Casa '" + nome + "' criada com sucesso!");
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    // ==========================================================
    // MENU DE DIVISÕES

    private void menuDivisoes() {
        System.out.print("\nID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            boolean voltar = false;

            while (!voltar) {
                System.out.println("\n── Divisões de '" + h.getNome() + "' ──");
                System.out.println("  1. Listar divisões");
                System.out.println("  2. Criar nova divisão");
                System.out.println("  3. Ver dispositivos de uma divisão");
                System.out.println("  0. Voltar");
                System.out.print("Opção: ");

                String opcao = scanner.nextLine().trim();
                switch (opcao) {
                    case "1": listarDivisoes(idCasa);       break;
                    case "2": criarDivisao(idCasa);         break;
                    case "3": verDispositivosDivisao(idCasa); break;
                    case "0": voltar = true;                break;
                    default:  System.out.println("⚠ Opção inválida.");
                }
            }

        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // LISTA DIVISÕES

    private void listarDivisoes(String idCasa) {
        try {
            House h = controller.getCasa(idCasa);
            List<Room> divisoes = h.getDivisoes();

            if (divisoes.isEmpty()) {
                System.out.println("  (Esta casa ainda não tem divisões.)");
                return;
            }

            System.out.println("\n── Divisões ──");
            for (Room r : divisoes) {
                System.out.printf("  [%s] %s | Dispositivos: %d | Consumo: %.1f Wh%n",
                        r.getId(), r.getNome(), r.getNumDispositivos(), r.getConsumoTotal());
            }
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // CRIA DIVISÃO

    private void criarDivisao(String idCasa) {
        System.out.println("\n── Criar Nova Divisão ──");

        System.out.print("ID da divisão (ex: r5): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome da divisão (ex: Sala, Quarto): ");
        String nome = scanner.nextLine().trim();

        try {
            Room r = new Room(id, nome);
            controller.adicionarDivisao(idCasa, r);
            System.out.println("✓ Divisão '" + nome + "' criada com sucesso!");
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        } catch (InvalidPermissionException e) {
            System.out.println("✗ Não tens permissão de administrador para esta casa.");
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro de sessão: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro: " + e.getMessage());
        }
    }

    // ==========================================================
    // VER DISPOSITIVOS DE UMA DIVISÃO

    private void verDispositivosDivisao(String idCasa) {
        System.out.print("ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            Room r = h.getDivisao(idDivisao);

            if (r == null) {
                System.out.println("✗ Divisão não encontrada.");
                return;
            }

            List<Device> dispositivos = r.getDispositivos();
            if (dispositivos.isEmpty()) {
                System.out.println("  (Esta divisão ainda não tem dispositivos.)");
                return;
            }

            System.out.println("\n── Dispositivos de '" + r.getNome() + "' ──");
            for (Device d : dispositivos) {
                System.out.printf("  [%s] %s %s | %s%n",
                        d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
            }

        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // ADICIONA DISPOSITIVO

    private void adicionarDispositivo() {
        System.out.println("\n── Adicionar Dispositivo ──");

        System.out.print("ID da casa: ");
        String idCasa = scanner.nextLine().trim();

        System.out.print("ID da divisão: ");
        String idDivisao = scanner.nextLine().trim();

        System.out.println("\nTipo de dispositivo:");
        System.out.println("  1. Lâmpada (Lamp)");
        System.out.println("  2. Lâmpada de cor (ColorLamp)");
        System.out.println("  3. Coluna (Speaker)");
        System.out.println("  4. Cortina (Curtain)");
        System.out.println("  5. Portão de garagem (GarageGate)");
        System.out.println("  6. Relé (Relay)");
        System.out.println("  7. Tomada (Socket)");
        System.out.print("Tipo: ");
        String tipo = scanner.nextLine().trim();

        System.out.print("ID do dispositivo (ex: d12): ");
        String id = scanner.nextLine().trim();

        System.out.print("Marca: ");
        String marca = scanner.nextLine().trim();

        System.out.print("Modelo: ");
        String modelo = scanner.nextLine().trim();

        System.out.print("Consumo (Wh): ");
        double consumo;
        try {
            consumo = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Valor de consumo inválido.");
            return;
        }

        Device d = criarDispositivo(tipo, id, marca, modelo, consumo);
        if (d == null) {
            System.out.println("✗ Tipo de dispositivo inválido.");
            return;
        }

        try {
            controller.adicionarDispositivo(idCasa, idDivisao, d);
            System.out.println("✓ Dispositivo adicionado com sucesso!");
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        } catch (RoomNotFoundException e) {
            System.out.println("✗ Divisão não encontrada.");
        } catch (InvalidPermissionException e) {
            System.out.println("✗ Não tens permissão de administrador para esta casa.");
        } catch (UserNotFoundException e) {
            System.out.println("✗ Erro de sessão: " + e.getMessage());
        }
    }

    // ==========================================================
    // CRIA DISPOSITIVO PELO TIPO

    private Device criarDispositivo(String tipo, String id, String marca,
                                     String modelo, double consumo) {
        switch (tipo) {
            case "1":
                System.out.print("Brilho (0-100): ");
                int brilho = lerInteiro(0, 100);
                return new Lamp(id, marca, modelo, consumo, brilho);

            case "2":
                System.out.print("Brilho (0-100): ");
                int brilhoCor = lerInteiro(0, 100);
                System.out.print("Temperatura de cor (2700-4000 K): ");
                int temp = lerInteiro(2700, 4000);
                return new ColorLamp(id, marca, modelo, consumo, brilhoCor, temp);

            case "3":
                System.out.print("Volume (0-100): ");
                int volume = lerInteiro(0, 100);
                return new Speaker(id, marca, modelo, consumo, volume);

            case "4":
                return new Curtain(id, marca, modelo, consumo);

            case "5":
                return new GarageGate(id, marca, modelo, consumo);

            case "6":
                return new Relay(id, marca, modelo, consumo);

            case "7":
                return new Socket(id, marca, modelo, consumo);

            default:
                return null;
        }
    }

    // ==========================================================
    // VER DETALHES DE UMA CASA

    private void verDetalhesCasa() {
        System.out.print("\nID da casa: ");
        String idCasa = scanner.nextLine().trim();

        try {
            House h = controller.getCasa(idCasa);
            System.out.println("\n── Detalhes de '" + h.getNome() + "' ──");
            System.out.println("  ID:      " + h.getId());
            System.out.println("  Nome:    " + h.getNome());
            System.out.println("  Morada:  " + h.getMorada());
            System.out.printf( "  Consumo total: %.1f Wh%n", h.getConsumoTotal());
            System.out.println("  Divisões:");

            for (Room r : h.getDivisoes()) {
                System.out.printf("    [%s] %s | %d dispositivos | %.1f Wh%n",
                        r.getId(), r.getNome(), r.getNumDispositivos(), r.getConsumoTotal());

                for (Device d : r.getDispositivos()) {
                    System.out.printf("       → [%s] %s %s | %s%n",
                            d.getId(), d.getMarca(), d.getModelo(), d.getEstadoDetalhado());
                }
            }
        } catch (HouseNotFoundException e) {
            System.out.println("✗ Casa não encontrada.");
        }
    }

    // ==========================================================
    // UTILITÁRIO: lê inteiro com limites

    private int lerInteiro(int min, int max) {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) return valor;
                System.out.printf("⚠ Introduz um valor entre %d e %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("⚠ Valor inválido. Tenta novamente: ");
            }
        }
    }
}