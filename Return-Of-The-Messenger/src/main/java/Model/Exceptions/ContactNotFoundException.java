package Model.Exceptions;

public class ContactNotFoundException extends Exception {

    public ContactNotFoundException() {
        super("no contact with such username was found");
    }
}
