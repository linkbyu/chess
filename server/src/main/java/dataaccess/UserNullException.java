package dataaccess;

public class UserNullException extends DataAccessException {

    public UserNullException(String message, Throwable ex) {
        super(message, ex);
    }
}
