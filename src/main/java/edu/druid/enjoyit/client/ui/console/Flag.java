package edu.druid.enjoyit.client.ui.console;

public enum Flag {
    LOGIN("--login "),
    REGISTER("--register "),
    GET_ROOMS("--room --list "),
    CONNECT_TO_ROOM("--room --connect "),
    CREATE_A_ROOM("--room --create "),
    LEAVE_THE_ROOM("--room --leave "),
    ROOM_STATUS("--room --info "),
    EXIT("--exit "),
    HELP("--help "),
    FLAG_KEYWORD("--");
    private final String value;

    Flag(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
