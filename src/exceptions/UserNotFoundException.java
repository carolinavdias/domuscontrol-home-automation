package exceptions;

// exceçao lancada quando se tenta aceder a um utilizador que nao existe
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String id) {
        super("Não foi encontrado nenhum utilizador com o id: " + id);
    }
}