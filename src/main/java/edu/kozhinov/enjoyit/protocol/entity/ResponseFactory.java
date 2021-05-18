package edu.kozhinov.enjoyit.protocol.entity;

import edu.kozhinov.enjoyit.base.component.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseFactory {
    private final JsonMapper jsonMapper;

    @Autowired
    public ResponseFactory(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public Response create(Status status) {
        return new Response(status);
    }

    public Response create(Command command, Object body) {
        return new Response(command, jsonMapper.writeValue(body));
    }

    public Response create(Command command, Status status) {
        return new Response(command, status);
    }

    public Response create(Command command, Status status, Object body) {
        return new Response(command, status, jsonMapper.writeValue(body));
    }

    public Response create(Command command, Status status, String statusMessage, Object body) {
        return new Response(command, status, statusMessage, jsonMapper.writeValue(body));
    }
}
