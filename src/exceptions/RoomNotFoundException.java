package exceptions;

// exceçao lancada quando se tenta aceder a uma divisão que nao existe
public class RoomNotFoundException extends Exception {

    public RoomNotFoundException(String id) {
        super("Não foi encontrada nenhuma divisao com o id: " + id);
    }
}