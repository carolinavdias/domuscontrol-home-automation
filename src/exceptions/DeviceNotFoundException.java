package exceptions;

// exceção lançda quando se tenta aceder a um dispositivo que não existe
public class DeviceNotFoundException extends Exception {

    public DeviceNotFoundException(String id) {
        super("Não foi encontrado nenhum dispositivo com o id: " + id);
    }
}