package Model.Exceptions;

public class NotFullFoucsedException extends Exception {

    public NotFullFoucsedException() {
        super("not focused on a user or a host and port");
    }
}
