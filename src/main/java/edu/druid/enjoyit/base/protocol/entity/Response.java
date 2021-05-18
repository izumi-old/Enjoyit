package edu.druid.enjoyit.base.protocol.entity;

import edu.druid.enjoyit.base.protocol.exception.ConversionException;

public class Response extends Data {
    Response(Status status) {
        super(status);
    }

    Response(Command command, String jsonBody) {
        super(Type.RESPONSE, command, jsonBody);
    }

    Response(Command command, Status status) {
        super(command, status);
    }

    Response(Command command, Status status, String jsonBody) {
        super(command, status, jsonBody);
    }

    Response(Command command, Status status, String statusMessage, String jsonBody) {
        super(Type.RESPONSE, command, status, statusMessage, jsonBody);
    }

    private Response(Data data) {
        super(data.getType(), data.getCommand(), data.getStatus(), data.getStatusMessage(),
                data.getJsonBody());
    }

    public static Response convert(Data data) throws ConversionException {
        if (data == null) {
            throw new ConversionException("Given data-message is null");
        }

        if (data.getType() != Type.RESPONSE) {
            throw new ConversionException("Given data-message is not a response");
        }

        return new Response(data);
    }
}
