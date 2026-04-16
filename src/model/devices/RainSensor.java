package model.devices;

//sensor de chuva, mede a pluviosidade atual
//usado pelas automacoes para por exemplo fechar cortinas quando esta a chover
public class RainSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    //limiar acima do qual consideramos que esta a chover
    private double limiarChuva;

    public RainSensor(String id, String marca, String modelo,
                      double consumoWh, double limiarChuva) {
        super(id, marca, modelo, consumoWh);
        this.limiarChuva = limiarChuva;
    }

    public RainSensor(RainSensor outro) {
        super(outro);
        this.limiarChuva = outro.limiarChuva;
    }

    public boolean estaAChover() {
        return getValorAtual() >= limiarChuva;
    }

    public double getLimiarChuva()            { return limiarChuva; }
    public void setLimiarChuva(double limiar) { this.limiarChuva = limiar; }

    @Override
    public String getEstadoDetalhado() {
        return "pluviosidade: " + getValorAtual() + " mm | "
             + (estaAChover() ? "esta a chover" : "nao esta a chover");
    }

    @Override
    public Device clone() {
        return new RainSensor(this);
    }
}