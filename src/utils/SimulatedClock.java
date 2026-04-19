package utils;

import java.io.Serializable;
import java.time.LocalDateTime;

// representa o relogio interno do sistema
// como a aplicacao é uma simulacao, o tempo nao avanca sozinho... temos de o avançar manualmente com os metodos desta classe


public class SimulatedClock implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime dataHoraAtual;

    // construtor, começa com a data e hora atuais do sistema
    public SimulatedClock() {
        this.dataHoraAtual = LocalDateTime.now();
    }

    // construtor que permite definir uma data e hora especifica
    public SimulatedClock(LocalDateTime dataHoraInicial) {
        this.dataHoraAtual = dataHoraInicial;
    }

    // avanca o tempo em minutos
    public void avancarMinutos(long minutos) {
        this.dataHoraAtual = this.dataHoraAtual.plusMinutes(minutos);
    }

    // avanca em horas
    public void avancarHoras(long horas) {
        this.dataHoraAtual = this.dataHoraAtual.plusHours(horas);
    }

    // avanca em dias
    public void avancarDias(long dias) {
        this.dataHoraAtual = this.dataHoraAtual.plusDays(dias);
    }

    // devolve a data e hora atual da simulação
    public LocalDateTime getDataHoraAtual() {
        return this.dataHoraAtual;
    }

    // permite definir manualmente a data e hora
    public void setDataHoraAtual(LocalDateTime dataHora) {
        if (dataHora == null)
            throw new IllegalArgumentException("A data e hora não podem ser null.");
        this.dataHoraAtual = dataHora;
    }

    @Override
    public String toString() {
        return "Data e hora atual da simulação: " + dataHoraAtual.toString();
    }
}