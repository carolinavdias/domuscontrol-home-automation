package view;

import controller.DomusController;
import exceptions.UserNotFoundException;
import model.users.User;
import utils.TestData;

import java.util.Scanner;

// ponto de entrada da interface de texto
// trata do login, registo e navegação para os outros menus
public class MainMenu {

    private DomusController controller;
    private Scanner scanner;

    public MainMenu() {
        this.controller = new DomusController();
        this.scanner = new Scanner(System.in);
        TestData.popular(controller);
    }

    //==========================================================
    // ARRANQUE

    public void iniciar() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        Bem-vindo ao DomusControl     ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean sair = false;
        while (!sair) {
            sair = mostrarMenuInicial();
        }

        System.out.println("\nAté logo! 👋");
        scanner.close();
    }

    //==========================================================
    // MENU INICIAL

    private boolean mostrarMenuInicial() {
        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│              Menu Inicial            │");
        System.out.println("├──────────────────────────────────────┤");
        System.out.println("│  1. Iniciar sessão                   │");
        System.out.println("│  2. Registar novo utilizador         │");
        System.out.println("│  0. Sair                             │");
        System.out.println("└──────────────────────────────────────┘");
        System.out.print("Opção: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                if (fazerLogin()) mostrarMenuPrincipal();
                return false;
            case "2":
                registarUtilizador();
                return false;
            case "0":
                return true;
            default:
                System.out.println("⚠ Opção inválida. Tenta novamente.");
                return false;
        }
    }

    //==========================================================
    // LOGIN

    private boolean fazerLogin() {
        System.out.println("\n── Iniciar Sessão ──");

        System.out.print("ID de utilizador: ");
        String id = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean sucesso = controller.autenticar(id, password);

        if (sucesso) {
            try {
                User u = controller.getUtilizadorAtual();
                System.out.println("✓ Sessão iniciada. Bem-vindo/a, " + u.getNome() + "!");
            } catch (UserNotFoundException e) {
                System.out.println("✓ Sessão iniciada.");
            }
        } else {
            System.out.println("✗ ID ou password incorretos.");
        }

        return sucesso;
    }

    //==========================================================
    // REGISTO

    private void registarUtilizador() {
        System.out.println("\n── Registar Utilizador ──");

        System.out.print("ID (ex: u3): ");
        String id = scanner.nextLine().trim();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            model.users.User novoUser = new model.users.User(id, nome, email, password);
            controller.registarUtilizador(novoUser);
            System.out.println("✓ Utilizador registado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Erro ao registar: " + e.getMessage());
        }
    }

    //==========================================================
    // MENU PRINCIPAL

    private void mostrarMenuPrincipal() {
        boolean sair = false;
        while (!sair) {
            try {
                User atual = controller.getUtilizadorAtual();
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║  Olá, " + padDir(atual.getNome(), 30) + " ║");
                System.out.println("╠══════════════════════════════════════╣");
                System.out.println("║  1. Gerir Casas e Divisões           ║");
                System.out.println("║  2. Gerir Dispositivos               ║");
                System.out.println("║  3. Cenários                         ║");
                System.out.println("║  4. Escalonamentos                   ║");
                System.out.println("║  5. Automações                       ║");
                System.out.println("║  6. Avançar tempo                    ║");
                System.out.println("║  7. Estatísticas                     ║");
                System.out.println("║  8. Guardar estado                   ║");
                System.out.println("║  9. Carregar estado                  ║");
                System.out.println("║  0. Terminar sessão                  ║");
                System.out.println("╚══════════════════════════════════════╝");
                System.out.print("Opção: ");

                switch (scanner.nextLine().trim()) {
                    case "1": new HouseMenu(controller, scanner).mostrar();    break;
                    case "2": new DeviceMenu(controller, scanner).mostrar();   break;
                    case "3": new ScenarioMenu(controller, scanner).mostrar(); break;
                    case "4": new SchedulingMenu(controller, scanner).mostrar(); break;
                    case "5": new AutomationMenu(controller, scanner).mostrar(); break;
                    case "6": avancarTempo();       break;
                    case "7": mostrarEstatisticas(); break;
                    case "8": guardarEstado();       break;
                    case "9": carregarEstado();      break;
                    case "0":
                        controller.terminarSessao();
                        System.out.println("✓ Sessão terminada.");
                        sair = true;
                        break;
                    default:
                        System.out.println("⚠ Opção inválida.");
                }

            } catch (UserNotFoundException e) {
                System.out.println("✗ Sessão expirada. Volta ao menu inicial.");
                sair = true;
            }
        }
    }

    //==========================================================
    // AVANÇAR TEMPO

    private void avancarTempo() {
        System.out.println("\n── Avançar Tempo de Simulação ──");
        System.out.println("  1. Avançar minutos");
        System.out.println("  2. Avançar horas");
        System.out.println("  3. Avançar dias");
        System.out.println("  0. Voltar");
        System.out.print("Opção: ");

        String opcao = scanner.nextLine().trim();
        if (opcao.equals("0")) return;

        System.out.print("Quantidade: ");
        try {
            long quantidade = Long.parseLong(scanner.nextLine().trim());
            switch (opcao) {
                case "1": controller.avancarTempo(quantidade);           break;
                case "2": controller.avancarTempo(quantidade * 60);      break;
                case "3": controller.avancarTempo(quantidade * 60 * 24); break;
                default:  System.out.println("⚠ Opção inválida."); return;
            }
            System.out.println("✓ Tempo avançado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("✗ Valor inválido.");
        }
    }

    //==========================================================
    // ESTATÍSTICAS

    private void mostrarEstatisticas() {
        System.out.println("\n── Estatísticas ──");

        model.house.House maisConsome = controller.getCasaQueMaisConsome();
        if (maisConsome != null) {
            System.out.printf("🏠 Casa que mais consome: %s (%.1f Wh)%n",
                    maisConsome.getNome(), maisConsome.getConsumoTotal());
        } else {
            System.out.println("🏠 Sem casas no sistema.");
        }

        System.out.println("\n📊 Top 3 divisões com mais dispositivos:");
        java.util.List<String> top3 = controller.getTop3DivisoesPorDispositivos();
        if (top3.isEmpty()) {
            System.out.println("  (sem dados)");
        } else {
            for (int i = 0; i < top3.size(); i++)
                System.out.println("  " + (i + 1) + ". " + top3.get(i));
        }

        System.out.print("\nID da casa para ver top 3 dispositivos (ou Enter para saltar): ");
        String idCasa = scanner.nextLine().trim();
        if (!idCasa.isBlank()) {
            try {
                System.out.println("Por ativações:");
                java.util.List<model.devices.Device> topAtiv =
                        controller.getTop3DispositivosPorActivacoes(idCasa);
                for (int i = 0; i < topAtiv.size(); i++)
                    System.out.println("  " + (i + 1) + ". " + topAtiv.get(i).getMarca()
                            + " " + topAtiv.get(i).getModelo()
                            + " | Ativações: " + topAtiv.get(i).getNumActivacoes());

                System.out.println("Por tempo ligado:");
                java.util.List<model.devices.Device> topTempo =
                        controller.getTop3DispositivosPorTempo(idCasa);
                for (int i = 0; i < topTempo.size(); i++)
                    System.out.println("  " + (i + 1) + ". " + topTempo.get(i).getMarca()
                            + " " + topTempo.get(i).getModelo()
                            + " | Tempo: " + topTempo.get(i).getTotalMinutosLigado() + " min");

            } catch (exceptions.HouseNotFoundException e) {
                System.out.println("✗ Casa não encontrada.");
            }
        }
    }

    //==========================================================
    // GUARDAR E CARREGAR ESTADO

    private void guardarEstado() {
        System.out.print("Nome do ficheiro (ex: estado.dat): ");
        String caminho = scanner.nextLine().trim();
        try {
            controller.gravarEstado(caminho);
            System.out.println("✓ Estado guardado em: " + caminho);
        } catch (java.io.IOException e) {
            System.out.println("✗ Erro ao guardar: " + e.getMessage());
        }
    }

    private void carregarEstado() {
        System.out.print("Nome do ficheiro (ex: estado.dat): ");
        String caminho = scanner.nextLine().trim();
        try {
            controller = DomusController.carregarEstado(caminho);
            System.out.println("✓ Estado carregado com sucesso!");
        } catch (Exception e) {
            System.out.println("✗ Erro ao carregar: " + e.getMessage());
        }
    }

    //==========================================================
    // UTILITÁRIO

    private String padDir(String texto, int tamanho) {
        if (texto.length() >= tamanho) return texto.substring(0, tamanho);
        return texto + " ".repeat(tamanho - texto.length());
    }

    public static void main(String[] args) {
        new MainMenu().iniciar();
    }
}