package Model.Exceptions;

public class NoUsernameAvailableException extends Exception {

    public NoUsernameAvailableException() {
        super("this username is unavailable");
    }

}
