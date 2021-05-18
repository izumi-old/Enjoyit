package edu.druid.enjoyit.base.protocol.entity;

public enum Status {
    NO("0"),
    OK("1"),
    BAD_REQUEST("100"),
    UNKNOWN_COMMAND("-1");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
