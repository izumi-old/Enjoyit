package edu.druid.enjoyit.base.protocol.entity;

import edu.druid.enjoyit.base.annotation.Factory;
import edu.druid.enjoyit.base.component.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Factory
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
