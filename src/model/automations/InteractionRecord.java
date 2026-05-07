package model.automations;

import java.io.Serializable;
import java.time.LocalDateTime;

// registo de uma interação com um dispositivo (ou leitura de sensor)
public class InteractionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idUtilizador;
    private String idDispositivo;
    private String nomeDispositivo;
    private String idCasa;
    private String tipoAcao; // "ligar", "desligar" ou "sensor"
    private LocalDateTime dataHora;
    private double valorSensor; // só relevante quando tipoAcao == "sensor"

    // construtor para ações sobre dispositivos
    public InteractionRecord(String idUtilizador, String idDispositivo,
                             String nomeDispositivo, String idCasa,
                             String tipoAcao, LocalDateTime dataHora) {
        this.idUtilizador   = idUtilizador;
        this.idDispositivo  = idDispositivo;
        this.nomeDispositivo = nomeDispositivo;
        this.idCasa         = idCasa;
        this.tipoAcao       = tipoAcao;
        this.dataHora       = dataHora;
        this.valorSensor    = 0;
    }

    // construtor para leituras de sensores
    public InteractionRecord(String idUtilizador, String idDispositivo,
                             String nomeDispositivo, String idCasa,
                             LocalDateTime dataHora, double valorSensor) {
        this(idUtilizador, idDispositivo, nomeDispositivo, idCasa, "sensor", dataHora);
        this.valorSensor = valorSensor;
    }

    public String getIdUtilizador()    { return idUtilizador; }
    public String getIdDispositivo()   { return idDispositivo; }
    public String getNomeDispositivo() { return nomeDispositivo; }
    public String getIdCasa()          { return idCasa; }
    public String getTipoAcao()        { return tipoAcao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getValorSensor()     { return valorSensor; }
}