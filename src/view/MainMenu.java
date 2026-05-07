package view;

import controller.DomusController;
import exceptions.UserNotFoundException;
import model.users.User;
import utils.TestData;
import utils.UIColors;

import java.util.Scanner;

// ponto de entrada da interface de texto
public class MainMenu {

    private DomusController controller;
    private Scanner scanner;

    // largura interna das caixas (entre os ║) — não alterar
    private static final int LARGURA = 46;

    public MainMenu() {
        this.controller = new DomusController();
        this.scanner = new Scanner(System.in);
        TestData.popular(controller);
    }

    //==========================================================
    // ARRANQUE

    public void iniciar() {
        mostrarBanner();
        boolean sair = false;
        while (!sair) {
            sair = mostrarMenuInicial();
        }
        System.out.println("\n" + UIColors.CYAN + "  Até logo! 👋" + UIColors.RESET + "\n");
        scanner.close();
    }

    private void mostrarBanner() {
        String c = UIColors.BCYAN;
        String b = UIColors.BOLD;
        String d = UIColors.DIM;
        String r = UIColors.RESET;

        // cada linha tem exatamente LARGURA (46) chars visíveis entre os │
        System.out.println();
        System.out.println(c + "  ┌──────────────────────────────────────────────┐" + r);
        System.out.println(c + "  |                                              |" + r);
        System.out.println(c + "  │" + r + bannerLinha("      /\\        " + b + "D O M U S C O N T R O L" + r, 16 + 23) + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("     /  \\       ─────────────────────────",  16 + 25) + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("    / /\\ \\      Automação Doméstica",       16 + 19) + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("   /_/  \\_\\",                              11)      + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("   | |  | |     " + d + "Grupo 099 · LEI 25/26" + r, 16 + 21) + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("   | |  | |",                              11)      + c + "│" + r);
        System.out.println(c + "  │" + r + bannerLinha("   |_|__|_|",                              11)      + c + "│" + r);
        System.out.println(c + "  │" + r + " ".repeat(LARGURA) + c + "│" + r);
        System.out.println(c + "  └──────────────────────────────────────────────┘" + r);
        System.out.println();
    }

    // imprime uma linha de banner com padding à direita até LARGURA
    // textoVisivel = número de caracteres visíveis no texto (ignorando ANSI)
    private String bannerLinha(String texto, int textoVisivel) {
        int padding = LARGURA - textoVisivel;
        if (padding < 0) padding = 0;
        return texto + " ".repeat(padding);
    }

    //==========================================================
    // MENUS — helpers de alinhamento

    private void imprimirBordaTopo() {
        System.out.println(UIColors.BCYAN + "  ╔══════════════════════════════════════════════╗" + UIColors.RESET);
    }

    private void imprimirBordaFundo() {
        System.out.println(UIColors.BCYAN + "  ╚══════════════════════════════════════════════╝" + UIColors.RESET);
    }

    private void imprimirSeparador() {
        System.out.println(UIColors.BCYAN + "  ╠══════════════════════════════════════════════╣" + UIColors.RESET);
    }

    // linha com número de opção destacado — alinhamento garantido
    // inner = "  " + num(2) + "  " + texto + padding + "  " = LARGURA
    private void imprimirLinhaMenu(String num, String texto) {
        int padding = Math.max(0, LARGURA - 8 - texto.length());
        System.out.println(UIColors.BCYAN + "  ║  " + UIColors.BYELLOW + num
            + UIColors.RESET + "  " + texto + " ".repeat(padding)
            + UIColors.BCYAN + "  ║" + UIColors.RESET);
    }

    // linha de cabeçalho com nome e id
    private void imprimirLinhaHeader(String nome, String id) {
        int padding = Math.max(1, LARGURA - 4 - nome.length() - id.length());
        System.out.println(UIColors.BCYAN + "  ║  " + UIColors.BOLD + nome
            + UIColors.RESET + " ".repeat(padding) + UIColors.DIM + id
            + UIColors.RESET + UIColors.BCYAN + "  ║" + UIColors.RESET);
    }

    //==========================================================
    // MENU INICIAL

    private boolean mostrarMenuInicial() {
        imprimirBordaTopo();
        imprimirLinhaMenu("  ", "Menu Inicial");
        imprimirSeparador();
        imprimirLinhaMenu(" 1", "Iniciar sessão");
        imprimirLinhaMenu(" 2", "Registar novo utilizador");
        imprimirLinhaMenu(" 0", "Sair");
        imprimirBordaFundo();
        System.out.print("  Opção: ");

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
                System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
                return false;
        }
    }

    //==========================================================
    // LOGIN

    private boolean fazerLogin() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Iniciar Sessão ──" + UIColors.RESET);

        System.out.print("  ID de utilizador: ");
        String id = scanner.nextLine().trim();

        System.out.print("  Password: ");
        String password = scanner.nextLine().trim();

        boolean sucesso = controller.autenticar(id, password);

        if (sucesso) {
            try {
                User u = controller.getUtilizadorAtual();
                System.out.println("  " + UIColors.BGREEN + "✓ Bem-vindo/a, " + u.getNome() + "!" + UIColors.RESET);
            } catch (UserNotFoundException e) {
                System.out.println("  " + UIColors.BGREEN + "✓ Sessão iniciada." + UIColors.RESET);
            }
        } else {
            System.out.println("  " + UIColors.BRED + "✗ ID ou password incorretos." + UIColors.RESET);
        }

        return sucesso;
    }

    //==========================================================
    // REGISTO

    private void registarUtilizador() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Registar Utilizador ──" + UIColors.RESET);

        System.out.print("  ID (ex: u3): ");
        String id = scanner.nextLine().trim();

        System.out.print("  Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("  Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("  Password: ");
        String password = scanner.nextLine().trim();

        try {
            model.users.User novoUser = new model.users.User(id, nome, email, password);
            controller.registarUtilizador(novoUser);
            System.out.println("  " + UIColors.BGREEN + "✓ Utilizador registado com sucesso!" + UIColors.RESET);
        } catch (IllegalArgumentException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro ao registar: " + e.getMessage() + UIColors.RESET);
        }
    }

    //==========================================================
    // MENU PRINCIPAL

    private void mostrarMenuPrincipal() {
        boolean sair = false;
        while (!sair) {
            try {
                User atual = controller.getUtilizadorAtual();
                System.out.println();
                imprimirBordaTopo();
                imprimirLinhaHeader(atual.getNome(), atual.getId());
                imprimirSeparador();
                imprimirLinhaMenu(" 1", "Gerir Casas e Divisões");
                imprimirLinhaMenu(" 2", "Gerir Dispositivos");
                imprimirLinhaMenu(" 3", "Cenários");
                imprimirLinhaMenu(" 4", "Escalonamentos");
                imprimirLinhaMenu(" 5", "Automações");
                imprimirLinhaMenu(" 6", "Avançar tempo");
                imprimirLinhaMenu(" 7", "Estatísticas");
                imprimirLinhaMenu(" 8", "Guardar estado");
                imprimirLinhaMenu(" 9", "Carregar estado");
                imprimirLinhaMenu("10", "Sugestões automáticas");
                imprimirLinhaMenu(" 0", "Terminar sessão");
                imprimirBordaFundo();
                System.out.print("  Opção: ");

                switch (scanner.nextLine().trim()) {
                    case "1":  new HouseMenu(controller, scanner).mostrar();      break;
                    case "2":  new DeviceMenu(controller, scanner).mostrar();     break;
                    case "3":  new ScenarioMenu(controller, scanner).mostrar();   break;
                    case "4":  new SchedulingMenu(controller, scanner).mostrar(); break;
                    case "5":  new AutomationMenu(controller, scanner).mostrar(); break;
                    case "6":  avancarTempo();        break;
                    case "7":  mostrarEstatisticas(); break;
                    case "8":  guardarEstado();        break;
                    case "9":  carregarEstado();       break;
                    case "10": new SuggestionMenu(controller, scanner).mostrar(); break;
                    case "0":
                        controller.terminarSessao();
                        System.out.println("  " + UIColors.BGREEN + "✓ Sessão terminada." + UIColors.RESET);
                        sair = true;
                        break;
                    default:
                        System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
                }

            } catch (UserNotFoundException e) {
                System.out.println("  " + UIColors.BRED + "✗ Sessão expirada. Volta ao menu inicial." + UIColors.RESET);
                sair = true;
            }
        }
    }

    //==========================================================
    // AVANÇAR TEMPO

    private void avancarTempo() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Avançar Tempo de Simulação ──" + UIColors.RESET);
        System.out.println("  " + UIColors.BYELLOW + "1" + UIColors.RESET + "  Avançar minutos");
        System.out.println("  " + UIColors.BYELLOW + "2" + UIColors.RESET + "  Avançar horas");
        System.out.println("  " + UIColors.BYELLOW + "3" + UIColors.RESET + "  Avançar dias");
        System.out.println("  " + UIColors.BYELLOW + "0" + UIColors.RESET + "  Voltar");
        System.out.print("  Opção: ");

        String opcao = scanner.nextLine().trim();
        if (opcao.equals("0")) return;

        System.out.print("  Quantidade: ");
        try {
            long quantidade = Long.parseLong(scanner.nextLine().trim());
            switch (opcao) {
                case "1": controller.avancarTempo(quantidade);           break;
                case "2": controller.avancarTempo(quantidade * 60);      break;
                case "3": controller.avancarTempo(quantidade * 60 * 24); break;
                default:  System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET); return;
            }
            System.out.println("  " + UIColors.BGREEN + "✓ Tempo avançado." + UIColors.RESET);
        } catch (NumberFormatException e) {
            System.out.println("  " + UIColors.BRED + "✗ Valor inválido." + UIColors.RESET);
        }
    }

    //==========================================================
    // ESTATÍSTICAS

    private void mostrarEstatisticas() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Estatísticas ──" + UIColors.RESET);

        model.house.House maisConsome = controller.getCasaQueMaisConsome();
        if (maisConsome != null) {
            System.out.printf("  🏠 Casa que mais consome: " + UIColors.BOLD + "%s" + UIColors.RESET + " (%.1f Wh)%n",
                    maisConsome.getNome(), maisConsome.getConsumoTotal());
        } else {
            System.out.println("  🏠 Sem casas no sistema.");
        }

        System.out.println("\n  📊 Top 3 divisões com mais dispositivos:");
        java.util.List<String> top3 = controller.getTop3DivisoesPorDispositivos();
        if (top3.isEmpty()) {
            System.out.println("  " + UIColors.DIM + "(sem dados)" + UIColors.RESET);
        } else {
            for (int i = 0; i < top3.size(); i++)
                System.out.println("  " + UIColors.BYELLOW + (i + 1) + UIColors.RESET + "  " + top3.get(i));
        }

        System.out.print("\n  ID da casa para ver top 3 dispositivos (ou Enter para saltar): ");
        String idCasa = scanner.nextLine().trim();
        if (!idCasa.isBlank()) {
            try {
                System.out.println("\n  Por ativações:");
                java.util.List<model.devices.Device> topAtiv =
                        controller.getTop3DispositivosPorActivacoes(idCasa);
                for (int i = 0; i < topAtiv.size(); i++)
                    System.out.println("  " + UIColors.BYELLOW + (i + 1) + UIColors.RESET + "  "
                        + topAtiv.get(i).getMarca() + " " + topAtiv.get(i).getModelo()
                        + UIColors.DIM + "  |  Ativações: " + topAtiv.get(i).getNumActivacoes() + UIColors.RESET);

                System.out.println("\n  Por tempo ligado:");
                java.util.List<model.devices.Device> topTempo =
                        controller.getTop3DispositivosPorTempo(idCasa);
                for (int i = 0; i < topTempo.size(); i++)
                    System.out.println("  " + UIColors.BYELLOW + (i + 1) + UIColors.RESET + "  "
                        + topTempo.get(i).getMarca() + " " + topTempo.get(i).getModelo()
                        + UIColors.DIM + "  |  Tempo: " + topTempo.get(i).getTotalMinutosLigado() + " min" + UIColors.RESET);

            } catch (exceptions.HouseNotFoundException e) {
                System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
            }
        }
    }

    //==========================================================
    // GUARDAR E CARREGAR ESTADO

    private void guardarEstado() {
        System.out.print("  Nome do ficheiro (ex: estado.dat): ");
        String caminho = scanner.nextLine().trim();
        try {
            controller.gravarEstado(caminho);
            System.out.println("  " + UIColors.BGREEN + "✓ Estado guardado em: " + caminho + UIColors.RESET);
        } catch (java.io.IOException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro ao guardar: " + e.getMessage() + UIColors.RESET);
        }
    }

    private void carregarEstado() {
        System.out.print("  Nome do ficheiro (ex: estado.dat): ");
        String caminho = scanner.nextLine().trim();
        try {
            controller = DomusController.carregarEstado(caminho);
            System.out.println("  " + UIColors.BGREEN + "✓ Estado carregado com sucesso!" + UIColors.RESET);
        } catch (Exception e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro ao carregar: " + e.getMessage() + UIColors.RESET);
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