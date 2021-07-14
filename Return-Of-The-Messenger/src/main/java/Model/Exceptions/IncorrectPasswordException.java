package Model.Exceptions;

public class IncorrectPasswordException extends Exception {

    public IncorrectPasswordException() {
        super("incorrect password");
    }
}
