package edu.kozhinov.enjoyit.protocol.exception;

public class EnjoyitProtocolException extends RuntimeException {
    public EnjoyitProtocolException() {
        super();
    }

    public EnjoyitProtocolException(String message) {
        super(message);
    }

    public EnjoyitProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnjoyitProtocolException(Throwable cause) {
        super(cause);
    }
}
