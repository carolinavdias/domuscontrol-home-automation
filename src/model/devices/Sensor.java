package model.devices;

// classe intermédia para todos os dispositivos que leem valores do ambiente
//por exemplo sensor de chuva, sensor de luminosidade, (...) etc
public abstract class Sensor extends Device {

    private static final long serialVersionUID = 1L;

    //valor atual lido pelo sensor
    private double valorAtual;

    public Sensor(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.valorAtual = 0.0;
    }

    public Sensor(Sensor outro) {
        super(outro);
        this.valorAtual = outro.valorAtual;
    }

    public double getValorAtual()          { return valorAtual; }
    public void setValorAtual(double v)    { this.valorAtual = v; }

    //cada sensor descreve o que está a medir à sua maneira
    @Override
    public String getEstadoDetalhado() {
        return String.format("valor atual: %.2f", valorAtual);
    }
}