package edu.kozhinov.enjoyit.base.component.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kozhinov.enjoyit.base.component.JsonMapper;
import edu.kozhinov.enjoyit.base.exception.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonMapperImpl implements JsonMapper {
    private final ObjectMapper mapper;

    @Autowired
    public JsonMapperImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String writeValue(Object o) throws ParseException {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            throw new ParseException(ex);
        }
    }

    @Override
    public <T> T readValue(String json, Class<T> clazz) throws ParseException {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new ParseException(ex);
        }
    }
}
