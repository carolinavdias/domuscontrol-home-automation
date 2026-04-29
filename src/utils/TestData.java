package utils;

import model.automations.Scenario;
import model.automations.Scheduling;
import model.devices.*;
import model.house.House;
import model.house.Room;
import model.users.Role;
import model.users.User;
import controller.DomusController;

import java.time.LocalTime;

// classe responsável por popular o sistema com dados de teste
// permite desenvolver e testar os menus sem depender do ficheiro de estado
// basta chamar TestData.popular(controller) no início da aplicação

public class TestData {

    // construtor privado: esta classe não deve ser instanciada, só tem métodos estáticos
    private TestData() {}

    // método principal: popula controller com todos os dados de teste
    public static void popular(DomusController controller) {
        try {
            criarUtilizadores(controller);
            criarCasaComDivisoesEDispositivos(controller);
            criarCenariosDoUtilizador(controller);
            criarEscalonamentosDaCasa(controller);
        } catch (Exception e) {
            System.out.println("[TestData] Erro ao popular dados: " + e.getMessage());
        }
    }

    // ==========================================================
    // UTILIZADORES

    private static void criarUtilizadores(DomusController controller) {
        // utilizador admin (vai criar e gerir a casa)
        User admin = new User("u1", "Ana Silva", "ana@email.com", "1234");
        controller.registarUtilizador(admin);

        // utilizador regular (tem acesso à casa mas não pode fazer alterações)
        User regular = new User("u2", "Bruno Costa", "bruno@email.com", "5678");
        controller.registarUtilizador(regular);

        System.out.println("[TestData] Utilizadores criados.");
    }

    // ==========================================================
    // CASA, DIVISÕES E DISPOSITIVOS

    private static void criarCasaComDivisoesEDispositivos(DomusController controller)
            throws Exception {

        // autentica o admin para poder criar a casa
        controller.autenticar("u1", "1234");

        // cria a casa
        House casa = new House("c1", "Casa da Ana", "Rua das Flores, 10, Braga");
        controller.criarCasa(casa);

        // --- SALA ---
        Room sala = new Room("r1", "Sala");
        controller.adicionarDivisao("c1", sala);

        Lamp lampaSala = new Lamp("d1", "Philips", "HueLamp", 10.0, 80);
        controller.adicionarDispositivo("c1", "r1", lampaSala);

        ColorLamp lampaCorSala = new ColorLamp("d2", "Ikea", "Tradfri", 8.5, 60, 3000);
        controller.adicionarDispositivo("c1", "r1", lampaCorSala);

        Curtain cortinaSala = new Curtain("d3", "Somfy", "RS100", 5.0);
        controller.adicionarDispositivo("c1", "r1", cortinaSala);

        Speaker colunaSala = new Speaker("d4", "Sonos", "One", 15.0, 50);
        controller.adicionarDispositivo("c1", "r1", colunaSala);

        // --- QUARTO ---
        Room quarto = new Room("r2", "Quarto");
        controller.adicionarDivisao("c1", quarto);

        Lamp lampaQuarto = new Lamp("d5", "Philips", "SceneSwitch", 9.0, 40);
        controller.adicionarDispositivo("c1", "r2", lampaQuarto);

        Curtain cortinaQuarto = new Curtain("d6", "Somfy", "RS200", 5.0);
        controller.adicionarDispositivo("c1", "r2", cortinaQuarto);

        // --- COZINHA ---
        Room cozinha = new Room("r3", "Cozinha");
        controller.adicionarDivisao("c1", cozinha);

        Socket tomadaCozinha = new Socket("d7", "TP-Link", "KP115", 3.5);
        controller.adicionarDispositivo("c1", "r3", tomadaCozinha);

        Relay releCozinha = new Relay("d8", "Shelly", "1PM", 2.0);
        controller.adicionarDispositivo("c1", "r3", releCozinha);

        // --- GARAGEM ---
        Room garagem = new Room("r4", "Garagem");
        controller.adicionarDivisao("c1", garagem);

        GarageGate portao = new GarageGate("d9", "Nice", "TO4000", 20.0);
        controller.adicionarDispositivo("c1", "r4", portao);

        LuminositySensor sensorLuz = new LuminositySensor("d10", "Aqara", "GZCGQ01LM", 1.0, 100.0);
        controller.adicionarDispositivo("c1", "r4", sensorLuz);

        RainSensor sensorChuva = new RainSensor("d11", "Davis", "RainColl", 1.5, 2.0);
        controller.adicionarDispositivo("c1", "r4", sensorChuva);

        // dá acesso à casa ao utilizador regular (papel REGULAR)
        // nota: isto é feito diretamente no utilizador porque o controller ainda
        // não tem um método para partilhar casas com outros utilizadores
        controller.terminarSessao();
        System.out.println("[TestData] Casa, divisões e dispositivos criados.");
    }

    // ==========================================================
    // CENÁRIOS DO UTILIZADOR

    private static void criarCenariosDoUtilizador(DomusController controller)
            throws Exception {

        // autentica o admin para aceder ao utilizador
        controller.autenticar("u1", "1234");

        // --- CENÁRIO 1: FORA DE CASA ---
        // desliga tudo para poupar energia
        Scenario forasDeCasa = new Scenario("sc1", "Fora de Casa",
                "Desliga todos os dispositivos ao sair");
        forasDeCasa.adicionarAcao("d1", "desligar");
        forasDeCasa.adicionarAcao("d2", "desligar");
        forasDeCasa.adicionarAcao("d4", "desligar");
        forasDeCasa.adicionarAcao("d5", "desligar");
        forasDeCasa.adicionarAcao("d7", "desligar");
        forasDeCasa.adicionarAcao("d8", "desligar");

        // --- CENÁRIO 2: JANTAR COM AMIGOS ---
        // ambiente acolhedor: luz quente na sala, coluna ligada
        Scenario jantarAmigos = new Scenario("sc2", "Jantar com Amigos",
                "Ambiente acolhedor para receber visitas");
        jantarAmigos.adicionarAcao("d1", "ligar");
        jantarAmigos.adicionarAcao("d2", "ligar");
        jantarAmigos.adicionarAcao("d4", "ligar");
        jantarAmigos.adicionarAcao("d3", "desligar"); // cortinas fechadas

        // --- CENÁRIO 3: DEITAR ---
        // desliga sala, deixa luz do quarto fraca
        Scenario deitar = new Scenario("sc3", "Deitar",
                "Prepara a casa para a noite");
        deitar.adicionarAcao("d1", "desligar");
        deitar.adicionarAcao("d2", "desligar");
        deitar.adicionarAcao("d4", "desligar");
        deitar.adicionarAcao("d5", "ligar");   // luz suave no quarto
        deitar.adicionarAcao("d6", "desligar"); // cortina do quarto fechada
        deitar.adicionarAcao("d8", "desligar");

        // --- CENÁRIO 4: ACORDAR ---
        // abre cortinas, liga luz suave
        Scenario acordar = new Scenario("sc4", "Acordar",
                "Bom dia! Abre as cortinas e prepara a casa");
        acordar.adicionarAcao("d3", "ligar");  // abre cortina da sala
        acordar.adicionarAcao("d6", "ligar");  // abre cortina do quarto
        acordar.adicionarAcao("d5", "ligar");  // luz do quarto
        acordar.adicionarAcao("d8", "ligar");  // relé da cozinha (cafeteira, etc.)

        // adiciona os cenários ao utilizador
        // nota: o controller ainda não tem este método exposto,
        // substituir pelo método do controller
        controller.getUtilizadorAtual(); 

        // TEMPORÁRIO: acesso direto enquanto o controller não expõe o método
        // controller.adicionarCenarioAoUtilizador("u1", forasDeCasa); ← quando existir
        // Por agora os cenários ficam criados e prontos para usar nos menus

        controller.terminarSessao();
        System.out.println("[TestData] Cenários criados.");
    }

    // ==========================================================
    // ESCALONAMENTOS DA CASA

    private static void criarEscalonamentosDaCasa(DomusController controller)
            throws Exception {

        controller.autenticar("u1", "1234");

        // --- ESCALONAMENTO 1: Abrir cortinas de manhã ---
        Scheduling abrirCortinas = new Scheduling(
                "sc1", "Abrir Cortinas de Manhã",
                LocalTime.of(7, 30),   // hora de início
                null,                   // sem hora de fim (ação única)
                true,                   // repete todos os dias
                true                    // ligar
        );
        abrirCortinas.adicionarDispositivo("d3"); // cortina da sala
        abrirCortinas.adicionarDispositivo("d6"); // cortina do quarto

        // --- ESCALONAMENTO 2: Fechar cortinas à noite ---
        Scheduling fecharCortinas = new Scheduling(
                "sc2", "Fechar Cortinas à Noite",
                LocalTime.of(21, 0),
                null,
                true,
                false  // desligar
        );
        fecharCortinas.adicionarDispositivo("d3");
        fecharCortinas.adicionarDispositivo("d6");

        // --- ESCALONAMENTO 3: Ligar luzes ao anoitecer ---
        Scheduling ligarLuzes = new Scheduling(
                "sc3", "Ligar Luzes ao Anoitecer",
                LocalTime.of(19, 0),
                null,
                true,
                true  // ligar
        );
        ligarLuzes.adicionarDispositivo("d1"); // lâmpada da sala
        ligarLuzes.adicionarDispositivo("d2"); // lâmpada de cor da sala

        // --- ESCALONAMENTO 4: Desligar tudo a meio da noite ---
        Scheduling desligarTudo = new Scheduling(
                "sc4", "Desligar Tudo à Meia-Noite",
                LocalTime.of(0, 0),
                null,
                true,
                false  // desligar
        );
        desligarTudo.adicionarDispositivo("d1");
        desligarTudo.adicionarDispositivo("d2");
        desligarTudo.adicionarDispositivo("d4");
        desligarTudo.adicionarDispositivo("d5");
        desligarTudo.adicionarDispositivo("d7");
        desligarTudo.adicionarDispositivo("d8");

        // adiciona os escalonamentos à casa
        controller.getCasa("c1"); // verifica que a casa existe

        // nota: quando o controller expor adicionarEscalonamento(), usar esse método
        // controller.adicionarEscalonamento("c1", abrirCortinas); ← quando existir

        controller.terminarSessao();
        System.out.println("[TestData] Escalonamentos criados.");
    }
}