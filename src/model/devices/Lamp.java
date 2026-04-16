package model.devices;

//lampada com controlo de intensidade de brilho; o brilho vai de 0 a 100 !
public class Lamp extends Actuator {

    private static final long serialVersionUID = 1L;

    private int brilho;

    public Lamp(String id, String marca, String modelo, double consumoWh, int brilho) {
        super(id, marca, modelo, consumoWh);
        this.brilho = brilho;
    }

    public Lamp(Lamp outro) {
        super(outro);
        this.brilho = outro.brilho;
    }

    public int getBrilho() { return brilho; }

    public void setBrilho(int brilho) {
        if (brilho < 0 || brilho > 100)
            throw new IllegalArgumentException("O brilho tem de ser entre 0 e 100.");
        this.brilho = brilho;
    }

    @Override
    public String getEstadoDetalhado() {
        return isLigado() ? "ligada | brilho: " + brilho + "%" : "desligada";
    }

    @Override
    public Device clone() {
        return new Lamp(this);
    }
}