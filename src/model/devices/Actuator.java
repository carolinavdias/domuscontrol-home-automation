package model.devices;

import java.time.LocalDateTime;

//classe intermédia para todos os dispositivos que atuam sobre algo
//por exemplo lampadas, colunas, cortinas, etc etc
//separa os atuadores dos sensores logo na hierarquia !!!
public abstract class Actuator extends Device {

    private static final long serialVersionUID = 1L;

    public Actuator(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
    }

    public Actuator(Actuator outro) {
        super(outro);
    }
}