package edu.druid.enjoyit.base.protocol.exception;

import edu.druid.enjoyit.base.exception.EnjoyitException;

public class ConversionException extends EnjoyitException {
    public ConversionException() {
        super();
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}
