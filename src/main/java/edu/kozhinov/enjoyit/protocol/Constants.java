package edu.kozhinov.enjoyit.protocol;

import static edu.kozhinov.enjoyit.server.utils.Constants.EMPTY_STRING;

public final class Constants {
    private Constants() {}

    public static final String DELIMITER = ":DELIMITER:";
    public static final String DATA_MESSAGE_DELIMITER = ":CDELIMITER:";
    public static final String MESSAGE_POINTER = "[M:]";
    public static final String EMPTY_STATUS_MESSAGE = EMPTY_STRING;
    public static final String EMPTY_JSON = "{}";

    public static final int BUFFER_SIZE = 1024*64;
}
