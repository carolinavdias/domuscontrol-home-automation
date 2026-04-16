package model.devices;

//lampada com controlo de brilho e tambem de cor
//a temperatura de cor vai dos 2700K (mais quente) ate aos 4000K (mais fria)

public class ColorLamp extends Lamp {

    private static final long serialVersionUID = 1L;

    private int temperaturaKelvin;

    public ColorLamp(String id, String marca, String modelo, double consumoWh,
                     int brilho, int temperaturaKelvin) {
        super(id, marca, modelo, consumoWh, brilho);
        setTemperaturaKelvin(temperaturaKelvin);
    }

    public ColorLamp(ColorLamp outro) {
        super(outro);
        this.temperaturaKelvin = outro.temperaturaKelvin;
    }

    public int getTemperaturaKelvin() { return temperaturaKelvin; }

    public void setTemperaturaKelvin(int temperaturaKelvin) {
        if (temperaturaKelvin < 2700 || temperaturaKelvin > 4000)
            throw new IllegalArgumentException("A temperatura tem de ser entre 2700K e 4000K.");
        this.temperaturaKelvin = temperaturaKelvin;
    }

    @Override
    public String getEstadoDetalhado() {
        return isLigado()
            ? "ligada | brilho: " + getBrilho() + "% | cor: " + temperaturaKelvin + "K"
            : "desligada";
    }

    @Override
    public Device clone() {
        return new ColorLamp(this);
    }
}