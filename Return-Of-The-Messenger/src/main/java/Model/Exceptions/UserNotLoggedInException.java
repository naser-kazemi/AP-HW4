package Model.Exceptions;

public class UserNotLoggedInException extends Exception {

    public UserNotLoggedInException() {
        super("you must login before logging out");
    }
}
