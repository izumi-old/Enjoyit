package edu.kozhinov.enjoyit.protocol.entity;

import static edu.kozhinov.enjoyit.protocol.Constants.EMPTY_JSON;
import static edu.kozhinov.enjoyit.protocol.Constants.EMPTY_STATUS_MESSAGE;

/*  Data format
TYPE:DELIMITER:
COMMAND:DELIMITER:
STATUS_CODE:DELIMITER:
[M:]STATUS_MESSAGE:DELIMITER:
JSON_BODY:COMMAND_DELIMITER: */
public class Data {
    private final Type type;
    private final Command command;
    private final Status status;
    private final String statusMessage;
    private final String jsonBody;

    protected Data(Command command) {
        this(Type.REQUEST, command, Status.NO, EMPTY_STATUS_MESSAGE, EMPTY_JSON);
    }

    protected Data(Status status) {
        this(Type.RESPONSE, Command.NO, status, EMPTY_STATUS_MESSAGE, EMPTY_JSON);
    }

    protected Data(Type type, Command command, String jsonBody) {
        this(type, command, Status.OK, EMPTY_STATUS_MESSAGE, jsonBody);
    }

    protected Data(Command command, Status status) {
        this(Type.RESPONSE, command, status, EMPTY_STATUS_MESSAGE, EMPTY_JSON);
    }

    protected Data(Command command, Status status, String jsonBody) {
        this(Type.RESPONSE, command, status, EMPTY_STATUS_MESSAGE, jsonBody);
    }

    public Data(Type type, Command command, Status status, String statusMessage, String jsonBody) {
        this.type = type;
        this.command = command;
        this.status = status;
        this.statusMessage = statusMessage != null ? statusMessage : EMPTY_STATUS_MESSAGE;
        this.jsonBody = jsonBody != null ? jsonBody : EMPTY_JSON;
    }

    public Type getType() {
        return type;
    }

    public Command getCommand() {
        return command;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    @Override
    public String toString() {
        return String.format("Type: {%s} Command: {%s}, status: {%s}, statusMessage: {%s}, body: %s",
                type.toString(), command.asString(), status.asString(), statusMessage, jsonBody);
    }

    public enum Type {
        REQUEST, RESPONSE
    }
}
