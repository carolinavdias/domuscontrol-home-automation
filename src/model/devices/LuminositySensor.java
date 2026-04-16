package model.devices;

//sensor de luminosidade, mede a luz ambiente
//usado pelas automacoes para por exemplo ligar lampadas quando esta escuro
public class LuminositySensor extends Sensor {

    private static final long serialVersionUID = 1L;

    //limiar abaixo do qual consideramos que esta escuro
    private double limiarEscuridao;

    public LuminositySensor(String id, String marca, String modelo,
                            double consumoWh, double limiarEscuridao) {
        super(id, marca, modelo, consumoWh);
        this.limiarEscuridao = limiarEscuridao;
    }

    public LuminositySensor(LuminositySensor outro) {
        super(outro);
        this.limiarEscuridao = outro.limiarEscuridao;
    }

    public boolean estaEscuro() {
        return getValorAtual() < limiarEscuridao;
    }

    public double getLimiarEscuridao()             { return limiarEscuridao; }
    public void setLimiarEscuridao(double limiar)  { this.limiarEscuridao = limiar; }

    @Override
    public String getEstadoDetalhado() {
        return "luminosidade: " + getValorAtual() + " lux | "
             + (estaEscuro() ? "esta escuro" : "ha luz suficiente");
    }

    @Override
    public Device clone() {
        return new LuminositySensor(this);
    }
}