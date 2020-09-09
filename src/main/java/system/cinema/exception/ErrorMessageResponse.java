package system.cinema.exception;

public enum ErrorMessageResponse {
    METHOD_ARGUMENT_NOT_VALID("The request does not contain all required components.");

    private final String message;

    ErrorMessageResponse(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
