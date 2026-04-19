package exceptions;

// exceção lancada quando se tenta aceder a uma casa que não existe
public class HouseNotFoundException extends Exception {

    public HouseNotFoundException(String id) {
        super("Não foi encontrada nenhuma casa com o id: " + id);
    }
}