package Model.Exceptions;

public class NoItemAvailableException extends Exception {

    public NoItemAvailableException() {
        super("no item is available");
    }
}
