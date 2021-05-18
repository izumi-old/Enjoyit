package edu.druid.enjoyit.base.protocol.entity;

import edu.druid.enjoyit.base.protocol.exception.ConversionException;

import static edu.druid.enjoyit.base.protocol.utils.Constants.EMPTY_JSON;

public class Request extends Data {
    Request(Command command) {
        super(Type.REQUEST, command, EMPTY_JSON);
    }

    Request(Command command, String jsonBody) {
        super(Type.REQUEST, command, jsonBody);
    }

    private Request(Data data) throws IllegalArgumentException {
        super(data.getType(), data.getCommand(), data.getStatus(), data.getStatusMessage(),
                data.getJsonBody());
    }

    public static Request convert(Data data) throws ConversionException {
        if (data == null) {
            throw new ConversionException("Given data-message is null");
        }

        if (data.getType() != Type.REQUEST) {
            throw new ConversionException("Given data-message is not a request");
        }

        return  new Request(data);
    }
}
