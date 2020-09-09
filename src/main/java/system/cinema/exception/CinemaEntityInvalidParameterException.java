package system.cinema.exception;

/**
 * Exception handler whenever an invalid parameter is being passed
 */
public class CinemaEntityInvalidParameterException extends IllegalArgumentException {

    public CinemaEntityInvalidParameterException() {
    }

    public CinemaEntityInvalidParameterException(String s) {
        super(s);
    }

    public CinemaEntityInvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
