package contests.services;

public class ContestsException extends Exception{
    public ContestsException() {}

    public ContestsException(String message) {
        super(message);
    }

    public ContestsException(String message, Throwable cause) {
        super(message, cause);
    }
}
