package contents.exceptions;

public class WrongSizeException extends RuntimeException {
    public WrongSizeException() {
    }

    public WrongSizeException(String message) {
        super(message);
    }

    public WrongSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongSizeException(Throwable cause) {
        super(cause);
    }

    public WrongSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
