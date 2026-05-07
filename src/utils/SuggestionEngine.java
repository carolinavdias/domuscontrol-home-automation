package utils;

import model.automations.Automation;
import model.automations.InteractionRecord;
import model.automations.Scheduling;
import model.automations.Suggestion;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

// analisa o histórico e sugere automações e escalonamentos
public class SuggestionEngine {

    // mínimo de ocorrências para considerar um padrão
    private static final int MIN_OCORRENCIAS = 3;

    private SuggestionEngine() {}

    public static List<Suggestion> gerarSugestoes(List<InteractionRecord> historico, String idCasa) {
        List<InteractionRecord> registosCasa = historico.stream()
            .filter(r -> r.getIdCasa().equals(idCasa))
            .collect(Collectors.toList());

        List<Suggestion> sugestoes = new ArrayList<>();
        sugestoes.addAll(sugerirEscalonamentos(registosCasa));
        sugestoes.addAll(sugerirAutomacoes(registosCasa));
        return sugestoes;
    }

    // padrão temporal: dispositivo ligado na mesma hora 3+ vezes → escalonamento diário
    private static List<Suggestion> sugerirEscalonamentos(List<InteractionRecord> registos) {
        Map<String, Long> contagens = registos.stream()
            .filter(r -> r.getTipoAcao().equals("ligar"))
            .collect(Collectors.groupingBy(
                r -> r.getIdDispositivo() + "|" + r.getDataHora().getHour(),
                Collectors.counting()
            ));

        List<Suggestion> sugestoes = new ArrayList<>();

        for (Map.Entry<String, Long> entrada : contagens.entrySet()) {
            if (entrada.getValue() < MIN_OCORRENCIAS) continue;

            String[] partes = entrada.getKey().split("\\|");
            String idDisp = partes[0];
            int hora = Integer.parseInt(partes[1]);

            String nomeDisp = registos.stream()
                .filter(r -> r.getIdDispositivo().equals(idDisp))
                .findFirst()
                .map(InteractionRecord::getNomeDispositivo)
                .orElse(idDisp);

            String id   = "sug_sched_" + idDisp + "_" + hora;
            String nome = "Ligar " + nomeDisp + " às " + String.format("%02d:00", hora);
            Scheduling s = new Scheduling(id, nome, LocalTime.of(hora, 0), null, true, true);
            s.adicionarDispositivo(idDisp);

            String desc = String.format(
                "O dispositivo '%s' foi ligado %d vezes às %02dh. Criar escalonamento diário às %02d:00?",
                nomeDisp, entrada.getValue(), hora, hora);

            sugestoes.add(Suggestion.deEscalonamento(desc, s));
        }

        return sugestoes;
    }

    // padrão sensor→dispositivo: se depois de uma leitura de sensor o utilizador
    // liga manualmente um dispositivo em menos de 5 minutos, 3+ vezes → automação
    private static List<Suggestion> sugerirAutomacoes(List<InteractionRecord> registos) {
        List<InteractionRecord> sensorEvts = registos.stream()
            .filter(r -> r.getTipoAcao().equals("sensor"))
            .collect(Collectors.toList());

        List<InteractionRecord> ligarEvts = registos.stream()
            .filter(r -> r.getTipoAcao().equals("ligar"))
            .collect(Collectors.toList());

        Map<String, Integer> correlacoes   = new HashMap<>();
        Map<String, Double>  somaLimiares  = new HashMap<>();
        Map<String, String>  nomesSensores = new HashMap<>();
        Map<String, String>  nomesDisps    = new HashMap<>();

        for (InteractionRecord sEvt : sensorEvts) {
            for (InteractionRecord lEvt : ligarEvts) {
                if (!lEvt.getDataHora().isAfter(sEvt.getDataHora())) continue;
                if (lEvt.getDataHora().isAfter(sEvt.getDataHora().plusMinutes(5))) continue;

                String chave = sEvt.getIdDispositivo() + "|" + lEvt.getIdDispositivo();
                correlacoes.merge(chave, 1, Integer::sum);
                somaLimiares.merge(chave, sEvt.getValorSensor(), Double::sum);
                nomesSensores.put(sEvt.getIdDispositivo(), sEvt.getNomeDispositivo());
                nomesDisps.put(lEvt.getIdDispositivo(), lEvt.getNomeDispositivo());
            }
        }

        List<Suggestion> sugestoes = new ArrayList<>();

        for (Map.Entry<String, Integer> entrada : correlacoes.entrySet()) {
            if (entrada.getValue() < MIN_OCORRENCIAS) continue;

            String[] partes   = entrada.getKey().split("\\|");
            String idSensor   = partes[0];
            String idDisp     = partes[1];
            double limiar     = somaLimiares.get(entrada.getKey()) / entrada.getValue();
            String nomeSensor = nomesSensores.getOrDefault(idSensor, idSensor);
            String nomeDisp   = nomesDisps.getOrDefault(idDisp, idDisp);

            String id      = "sug_auto_" + idSensor + "_" + idDisp;
            String condicao = String.format("Valor de '%s' ≥ %.1f", nomeSensor, limiar);
            Automation a = new Automation(id, "Auto: " + nomeDisp + " via " + nomeSensor,
                condicao, idSensor, limiar, "ligar");
            a.adicionarDispositivo(idDisp);

            String desc = String.format(
                "Após '%s' atingir %.1f, '%s' foi ligado %d vezes. Criar automação?",
                nomeSensor, limiar, nomeDisp, entrada.getValue());

            sugestoes.add(Suggestion.deAutomacao(desc, a));
        }

        return sugestoes;
    }
}