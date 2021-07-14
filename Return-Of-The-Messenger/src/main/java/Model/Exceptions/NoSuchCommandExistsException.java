package Model.Exceptions;

public class NoSuchCommandExistsException extends Exception {

    public NoSuchCommandExistsException() {
        super("No such Command exists!");
    }
}

