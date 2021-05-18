package edu.druid.enjoyit.base.protocol.exception;

import edu.druid.enjoyit.base.exception.EnjoyitException;

public class MappingException extends EnjoyitException {
    public MappingException() {
        super();
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }
}
