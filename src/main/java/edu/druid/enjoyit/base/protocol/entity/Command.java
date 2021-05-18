package edu.druid.enjoyit.base.protocol.entity;

import static edu.druid.enjoyit.server.utils.Constants.EMPTY_STRING;

public enum Command {
    NO(EMPTY_STRING),
    MESSAGE("Mes"),
    MESSAGES("MsS"),
    MESSAGE_LOST("MsL"),
    GET_ROOMS("GeR"),
    CREATE_A_ROOM("CAR"),
    CONNECT_TO_ROOM("CTR"),
    LEAVE_THE_ROOM("LTR"),
    ROOM_STATUS("RoS"),
    LOGIN("Log"),
    REGISTER("Reg"),
    DISCONNECT("Dis");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
