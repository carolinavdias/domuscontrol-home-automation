package model.devices;
//o dispositivo mais simples do sistema, so liga e desliga


public class Relay extends Actuator {

    private static final long serialVersionUID = 1L;

    public Relay(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
    }

    public Relay(Relay outro) {
        super(outro);
    }

    @Override
    public String getEstadoDetalhado() {
        return isLigado() ? "ligado" : "desligado";
    }

    @Override
    public Device clone() {
        return new Relay(this);
    }
}

//-->equivale a um interruptor inteligente
