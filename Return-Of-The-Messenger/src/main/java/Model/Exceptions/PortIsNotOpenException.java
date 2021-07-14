package Model.Exceptions;

public class PortIsNotOpenException extends Exception {

    public PortIsNotOpenException() {
        super("the port you specified was not open");
    }
}
