package model.devices;

//tomada inteligente, funciona como um rele mas representa uma tomada
public class Socket extends Actuator {

    private static final long serialVersionUID = 1L;

    public Socket(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
    }

    public Socket(Socket outro) {
        super(outro);
    }

    @Override
    public String getEstadoDetalhado() {
        return isLigado() ? "ligada" : "desligada";
    }

    @Override
    public Device clone() {
        return new Socket(this);
    }
}