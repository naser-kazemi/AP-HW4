package Model.Exceptions;

public class CouldNotSendMessageException extends Exception {

    public CouldNotSendMessageException() {
        super("could not send message");
    }
}
