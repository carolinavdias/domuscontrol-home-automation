package exceptions;

// exceção lancada quando um utilizador tenta fazer algo que não tem permissão
// por ex: um utilizador regular tentar criar uma divisão (por aí...)

public class InvalidPermissionException extends Exception {

    public InvalidPermissionException(String mensagem) {
        super(mensagem);
    }
}