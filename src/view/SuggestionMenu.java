package view;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import model.automations.Suggestion;
import utils.UIColors;

import java.util.List;
import java.util.Scanner;

// menu para apresentar e aceitar sugestões automáticas
public class SuggestionMenu {

    private DomusController controller;
    private Scanner scanner;

    public SuggestionMenu(DomusController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner    = scanner;
    }

    public void mostrar() {
        System.out.println("\n" + UIColors.BCYAN + "  ── Sugestões Automáticas ──" + UIColors.RESET);
        System.out.print("  ID da casa a analisar: ");
        String idCasa = scanner.nextLine().trim();

        List<Suggestion> sugestoes;
        try {
            sugestoes = controller.gerarSugestoes(idCasa);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Casa não encontrada." + UIColors.RESET);
            return;
        }

        if (sugestoes.isEmpty()) {
            System.out.println("  " + UIColors.DIM + "ℹ Ainda não há interações suficientes para gerar sugestões." + UIColors.RESET);
            System.out.println("  " + UIColors.DIM + "  Utiliza mais os dispositivos e tenta novamente." + UIColors.RESET);
            return;
        }

        System.out.println("\n  Foram encontradas " + UIColors.BYELLOW + sugestoes.size() + UIColors.RESET + " sugestão(ões):\n");

        for (int i = 0; i < sugestoes.size(); i++) {
            Suggestion s = sugestoes.get(i);
            String tipo = s.getTipo() == Suggestion.Tipo.ESCALONAMENTO
                    ? UIColors.BCYAN + "📅 ESCALONAMENTO" + UIColors.RESET
                    : UIColors.BYELLOW + "⚡ AUTOMAÇÃO" + UIColors.RESET;
            System.out.println("  " + UIColors.BOLD + "[" + (i + 1) + "]" + UIColors.RESET + " " + tipo);
            System.out.println("      " + s.getDescricao());
        }

        System.out.print("\n  Número da sugestão a aceitar (0 para voltar): ");
        try {
            int opcao = Integer.parseInt(scanner.nextLine().trim());
            if (opcao == 0) return;

            if (opcao < 1 || opcao > sugestoes.size()) {
                System.out.println("  " + UIColors.BYELLOW + "⚠ Opção inválida." + UIColors.RESET);
                return;
            }

            controller.aceitarSugestao(idCasa, sugestoes.get(opcao - 1));
            System.out.println("  " + UIColors.BGREEN + "✓ Sugestão aceite e adicionada ao sistema!" + UIColors.RESET);

        } catch (NumberFormatException e) {
            System.out.println("  " + UIColors.BRED + "✗ Valor inválido." + UIColors.RESET);
        } catch (HouseNotFoundException e) {
            System.out.println("  " + UIColors.BRED + "✗ Erro ao aceitar sugestão: " + e.getMessage() + UIColors.RESET);
        }
    }
}