package edu.kozhinov.enjoyit.protocol.entity;

public enum SimpleRequest {
    ROOM_STATUS(new Request(Command.ROOM_STATUS)),
    GET_ROOMS(new Request(Command.GET_ROOMS)),
    LEAVE_THE_ROOM(new Request(Command.LEAVE_THE_ROOM)),
    DISCONNECT(new Request(Command.DISCONNECT));
    private final Request request;

    SimpleRequest(Request request) {
        this.request = request;
    }

    public Request value() {
        return request;
    }
}
