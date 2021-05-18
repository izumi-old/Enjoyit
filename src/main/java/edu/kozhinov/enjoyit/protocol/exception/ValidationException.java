package edu.kozhinov.enjoyit.protocol.exception;

public class ValidationException extends EnjoyitProtocolException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
