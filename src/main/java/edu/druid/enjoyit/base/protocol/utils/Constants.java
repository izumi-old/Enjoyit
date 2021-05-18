package edu.druid.enjoyit.base.protocol.utils;

import static edu.druid.enjoyit.server.utils.Constants.EMPTY_STRING;

public final class Constants {
    private Constants(){}

    public static final String GENERAL_LOGGER_NAME = "enjoyit";

    public static final String DELIMITER = ":DELIMITER:";
    public static final String DATA_MESSAGE_DELIMITER = ":CDELIMITER:";
    public static final String MESSAGE_POINTER = "[M:]";
    public static final String EMPTY_STATUS_MESSAGE = EMPTY_STRING;
    public static final String EMPTY_JSON = "{}";

    public static final int BUFFER_SIZE = 1024*64;
}
