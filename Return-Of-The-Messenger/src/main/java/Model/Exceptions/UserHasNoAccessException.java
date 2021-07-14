package Model.Exceptions;

public class UserHasNoAccessException extends Exception {

    public UserHasNoAccessException() {
        super("you must login to access this feature");
    }
}
