package dataaccess.exception;

public class UserNullException extends DataAccessException {

    public UserNullException(String message){
        super(message);
    }
    public UserNullException(String message, Throwable ex) {
        super(message, ex);
    }
}
