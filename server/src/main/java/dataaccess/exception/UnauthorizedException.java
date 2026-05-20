package dataaccess.exception;

public class UnauthorizedException extends Exception {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String invalidAuthToken) {
        super(message);
    }

}
