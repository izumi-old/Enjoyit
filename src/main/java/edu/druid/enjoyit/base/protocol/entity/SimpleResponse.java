package edu.druid.enjoyit.base.protocol.entity;

public enum SimpleResponse {
    UNKNOWN_COMMAND(new Response(Status.UNKNOWN_COMMAND));
    private final Response response;

    SimpleResponse(Response response) {
        this.response = response;
    }

    public Response value() {
        return response;
    }
}
