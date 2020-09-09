package system.cinema.exception;

/**
 * Custom exception handler whenever an entity from the cinema domain is not found.
 */
public class CinemaEntityNotFoundException extends RuntimeException {

    public CinemaEntityNotFoundException() {}

    public CinemaEntityNotFoundException(String message) {
        super(message);
    }

    public CinemaEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
