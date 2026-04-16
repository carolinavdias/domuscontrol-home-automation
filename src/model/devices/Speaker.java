package model.devices;

//coluna de som com controlo de volume
//o volume vai de 0 a 100
public class Speaker extends Actuator {

    private static final long serialVersionUID = 1L;

    private int volume;

    public Speaker(String id, String marca, String modelo, double consumoWh, int volume) {
        super(id, marca, modelo, consumoWh);
        this.volume = volume;
    }

    public Speaker(Speaker outro) {
        super(outro);
        this.volume = outro.volume;
    }

    public int getVolume() { return volume; }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100)
            throw new IllegalArgumentException("O volume tem de ser entre 0 e 100.");
        this.volume = volume;
    }

    @Override
    public String getEstadoDetalhado() {
        return isLigado() ? "ligada | volume: " + volume + "%" : "desligada";
    }

    @Override
    public Device clone() {
        return new Speaker(this);
    }
}