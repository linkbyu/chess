package dataaccess.exception;

public class BadRequestException extends DataAccessException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex){
        super(message, ex);
    }
}
