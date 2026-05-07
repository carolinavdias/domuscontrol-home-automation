package utils;

import controller.DomusController;
import exceptions.HouseNotFoundException;
import exceptions.UserNotFoundException;
import model.automations.Automation;
import model.automations.Scenario;
import model.automations.Scheduling;
import model.devices.*;
import model.house.House;
import model.house.Room;
import model.users.User;

import java.time.LocalTime;

// classe responsável por popular o sistema com dados de teste
// permite desenvolver e testar os menus sem depender do ficheiro de estado
// basta chamar TestData.popular(controller) no início da aplicação

public class TestData {

    // construtor privado: esta classe não deve ser instanciada
    private TestData() {}

    // método principal: popula o controller com todos os dados de teste
    public static void popular(DomusController controller) {
        try {
            criarUtilizadores(controller);
            criarCasaComDivisoesEDispositivos(controller);
            criarCenariosDoUtilizador(controller);
            criarEscalonamentosDaCasa(controller);
            criarAutomacoesDaCasa(controller);
            System.out.println("[TestData] Sistema populado com sucesso!");
        } catch (Exception e) {
            System.out.println("[TestData] Erro ao popular dados: " + e.getMessage());
        }
    }

    // ==========================================================
    // UTILIZADORES

    private static void criarUtilizadores(DomusController controller) {
        User admin = new User("u1", "Ana Silva", "ana@email.com", "1234");
        controller.registarUtilizador(admin);

        User regular = new User("u2", "Bruno Costa", "bruno@email.com", "5678");
        controller.registarUtilizador(regular);
    }

    // ==========================================================
    // CASA, DIVISÕES E DISPOSITIVOS

    private static void criarCasaComDivisoesEDispositivos(DomusController controller)
            throws Exception {

        controller.autenticar("u1", "1234");

        House casa = new House("c1", "Casa da Ana", "Rua das Flores, 10, Braga");
        controller.criarCasa(casa);

        // --- SALA ---
        Room sala = new Room("r1", "Sala");
        controller.adicionarDivisao("c1", sala);

        controller.adicionarDispositivo("c1", "r1",
                new Lamp("d1", "Philips", "HueLamp", 10.0, 80));
        controller.adicionarDispositivo("c1", "r1",
                new ColorLamp("d2", "Ikea", "Tradfri", 8.5, 60, 3000));
        controller.adicionarDispositivo("c1", "r1",
                new Curtain("d3", "Somfy", "RS100", 5.0));
        controller.adicionarDispositivo("c1", "r1",
                new Speaker("d4", "Sonos", "One", 15.0, 50));

        // --- QUARTO ---
        Room quarto = new Room("r2", "Quarto");
        controller.adicionarDivisao("c1", quarto);

        controller.adicionarDispositivo("c1", "r2",
                new Lamp("d5", "Philips", "SceneSwitch", 9.0, 40));
        controller.adicionarDispositivo("c1", "r2",
                new Curtain("d6", "Somfy", "RS200", 5.0));

        // --- COZINHA ---
        Room cozinha = new Room("r3", "Cozinha");
        controller.adicionarDivisao("c1", cozinha);

        controller.adicionarDispositivo("c1", "r3",
                new Socket("d7", "TP-Link", "KP115", 3.5));
        controller.adicionarDispositivo("c1", "r3",
                new Relay("d8", "Shelly", "1PM", 2.0));

        // --- GARAGEM ---
        Room garagem = new Room("r4", "Garagem");
        controller.adicionarDivisao("c1", garagem);

        controller.adicionarDispositivo("c1", "r4",
                new GarageGate("d9", "Nice", "TO4000", 20.0));
        controller.adicionarDispositivo("c1", "r4",
                new LuminositySensor("d10", "Aqara", "GZCGQ01LM", 1.0, 100.0));
        controller.adicionarDispositivo("c1", "r4",
                new RainSensor("d11", "Davis", "RainColl", 1.5, 2.0));

        controller.terminarSessao();
    }

    // ==========================================================
    // CENÁRIOS DO UTILIZADOR

    private static void criarCenariosDoUtilizador(DomusController controller)
            throws UserNotFoundException {

        controller.autenticar("u1", "1234");

        // --- CENÁRIO 1: FORA DE CASA ---
        Scenario forasDeCasa = new Scenario("sc1", "Fora de Casa",
                "Desliga todos os dispositivos ao sair");
        forasDeCasa.adicionarAcao("d1", "desligar");
        forasDeCasa.adicionarAcao("d2", "desligar");
        forasDeCasa.adicionarAcao("d4", "desligar");
        forasDeCasa.adicionarAcao("d5", "desligar");
        forasDeCasa.adicionarAcao("d7", "desligar");
        forasDeCasa.adicionarAcao("d8", "desligar");
        controller.adicionarCenarioAoUtilizador(forasDeCasa);

        // --- CENÁRIO 2: JANTAR COM AMIGOS ---
        Scenario jantarAmigos = new Scenario("sc2", "Jantar com Amigos",
                "Ambiente acolhedor para receber visitas");
        jantarAmigos.adicionarAcao("d1", "ligar");
        jantarAmigos.adicionarAcao("d2", "ligar");
        jantarAmigos.adicionarAcao("d4", "ligar");
        jantarAmigos.adicionarAcao("d3", "desligar");
        controller.adicionarCenarioAoUtilizador(jantarAmigos);

        // --- CENÁRIO 3: DEITAR ---
        Scenario deitar = new Scenario("sc3", "Deitar",
                "Prepara a casa para a noite");
        deitar.adicionarAcao("d1", "desligar");
        deitar.adicionarAcao("d2", "desligar");
        deitar.adicionarAcao("d4", "desligar");
        deitar.adicionarAcao("d5", "ligar");
        deitar.adicionarAcao("d6", "desligar");
        deitar.adicionarAcao("d8", "desligar");
        controller.adicionarCenarioAoUtilizador(deitar);

        // --- CENÁRIO 4: ACORDAR ---
        Scenario acordar = new Scenario("sc4", "Acordar",
                "Bom dia! Abre as cortinas e prepara a casa");
        acordar.adicionarAcao("d3", "ligar");
        acordar.adicionarAcao("d6", "ligar");
        acordar.adicionarAcao("d5", "ligar");
        acordar.adicionarAcao("d8", "ligar");
        controller.adicionarCenarioAoUtilizador(acordar);

        controller.terminarSessao();
    }

    // ==========================================================
    // ESCALONAMENTOS DA CASA

    private static void criarEscalonamentosDaCasa(DomusController controller)
            throws UserNotFoundException, HouseNotFoundException {

        controller.autenticar("u1", "1234");

        // --- ESCALONAMENTO 1: Abrir cortinas de manhã ---
        Scheduling abrirCortinas = new Scheduling(
                "esc1", "Abrir Cortinas de Manhã",
                LocalTime.of(7, 30), null, true, true);
        abrirCortinas.adicionarDispositivo("d3");
        abrirCortinas.adicionarDispositivo("d6");
        controller.adicionarEscalonamento("c1", abrirCortinas);

        // --- ESCALONAMENTO 2: Fechar cortinas à noite ---
        Scheduling fecharCortinas = new Scheduling(
                "esc2", "Fechar Cortinas à Noite",
                LocalTime.of(21, 0), null, true, false);
        fecharCortinas.adicionarDispositivo("d3");
        fecharCortinas.adicionarDispositivo("d6");
        controller.adicionarEscalonamento("c1", fecharCortinas);

        // --- ESCALONAMENTO 3: Ligar luzes ao anoitecer ---
        Scheduling ligarLuzes = new Scheduling(
                "esc3", "Ligar Luzes ao Anoitecer",
                LocalTime.of(19, 0), null, true, true);
        ligarLuzes.adicionarDispositivo("d1");
        ligarLuzes.adicionarDispositivo("d2");
        controller.adicionarEscalonamento("c1", ligarLuzes);

        // --- ESCALONAMENTO 4: Desligar tudo à meia-noite ---
        Scheduling desligarTudo = new Scheduling(
                "esc4", "Desligar Tudo à Meia-Noite",
                LocalTime.of(0, 0), null, true, false);
        desligarTudo.adicionarDispositivo("d1");
        desligarTudo.adicionarDispositivo("d2");
        desligarTudo.adicionarDispositivo("d4");
        desligarTudo.adicionarDispositivo("d5");
        desligarTudo.adicionarDispositivo("d7");
        desligarTudo.adicionarDispositivo("d8");
        controller.adicionarEscalonamento("c1", desligarTudo);

        controller.terminarSessao();
    }

    //==========================================================
    // AUTOMAÇÕES DA CASA

    private static void criarAutomacoesDaCasa(DomusController controller)
            throws UserNotFoundException, HouseNotFoundException {

        controller.autenticar("u1", "1234");

        // automação 1: se está a chover, fecha as cortinas
        Automation fecharCortinas = new Automation(
                "auto1",
                "Fechar Cortinas se Chover",
                "Fecha as cortinas quando o sensor de chuva deteta pluviosidade",
                "d11", 2.0, "desligar");
        fecharCortinas.adicionarDispositivo("d3");
        fecharCortinas.adicionarDispositivo("d6");
        controller.adicionarAutomacao("c1", fecharCortinas);

        // automação 2: se está escuro, liga as luzes
        Automation ligarLuzes = new Automation(
                "auto2",
                "Ligar Luzes se Estiver Escuro",
                "Liga as luzes quando a luminosidade desce abaixo do limiar",
                "d10", 100.0, "ligar");
        ligarLuzes.adicionarDispositivo("d1");
        ligarLuzes.adicionarDispositivo("d2");
        controller.adicionarAutomacao("c1", ligarLuzes);

        controller.terminarSessao();
    }
}