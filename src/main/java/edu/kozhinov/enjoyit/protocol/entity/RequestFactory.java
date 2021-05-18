package edu.kozhinov.enjoyit.protocol.entity;

import edu.kozhinov.enjoyit.core.component.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestFactory {
    private final JsonMapper jsonMapper;

    @Autowired
    public RequestFactory(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public Request create(Command command, Object body) {
        return new Request(command, jsonMapper.writeValue(body));
    }
}
