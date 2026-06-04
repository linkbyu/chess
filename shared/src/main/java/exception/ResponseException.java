package exception;


public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        BadRequest,
        Unauthorized,
        AlreadyTakenError
    }

    public ResponseException(Code code, String message) {
        super(message);
    }


    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.BadRequest;
            case 401 -> Code.Unauthorized;
            case 403 -> Code.AlreadyTakenError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }
}
