package model.devices;

//cortina com controlo de abertura
//a abertura vai de 0 (FECHADA) a 100 (ABERTA por completo)
public class Curtain extends Actuator {

    private static final long serialVersionUID = 1L;

    private int percentagemAbertura;

    public Curtain(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.percentagemAbertura = 0;
    }

    public Curtain(Curtain outro) {
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
        return new Curtain(this);
    }
}