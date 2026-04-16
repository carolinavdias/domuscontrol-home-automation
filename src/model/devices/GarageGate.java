package model.devices;

//portao de garagem com controlo de abertura
public class GarageGate extends Actuator {

    private static final long serialVersionUID = 1L;

    private int percentagemAbertura;

    public GarageGate(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.percentagemAbertura = 0;
    }

    public GarageGate(GarageGate outro) {
        super(outro);
        this.percentagemAbertura = outro.percentagemAbertura;
    }

    public int getPercentagemAbertura() { return percentagemAbertura; }

    public void setPercentagemAbertura(int percentagemAbertura) {
        if (percentagemAbertura < 0 || percentagemAbertura > 100)
            throw new IllegalArgumentException("A abertura tem de ser entre 0 e 100.");
        this.percentagemAbertura = percentagemAbertura;
    }

    public void abrirTotalmente()  { this.percentagemAbertura = 100; }
    public void fecharTotalmente() { this.percentagemAbertura = 0; }

    @Override
    public String getEstadoDetalhado() {
        return "abertura: " + percentagemAbertura + "%";
    }

    @Override
    public Device clone() {
        return new GarageGate(this);
    }
}

//funciona igual a uma cortina mas representa um portao de garagem
