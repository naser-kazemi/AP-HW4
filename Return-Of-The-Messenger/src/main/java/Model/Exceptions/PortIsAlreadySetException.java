package Model.Exceptions;

public class PortIsAlreadySetException extends Exception {


    public PortIsAlreadySetException() {
        super("the port is already set");
    }
}
