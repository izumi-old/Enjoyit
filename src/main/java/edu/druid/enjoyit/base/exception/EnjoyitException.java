package edu.druid.enjoyit.base.exception;

public class EnjoyitException extends RuntimeException {
    public EnjoyitException() {
        super();
    }

    public EnjoyitException(String message) {
        super(message);
    }

    public EnjoyitException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnjoyitException(Throwable cause) {
        super(cause);
    }
}
