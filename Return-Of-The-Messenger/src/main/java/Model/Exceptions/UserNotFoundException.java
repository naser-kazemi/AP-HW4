package Model.Exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("user not found");
    }
}
