package edu.kozhinov.enjoyit.core.component.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kozhinov.enjoyit.core.component.JsonMapper;
import edu.kozhinov.enjoyit.core.exception.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JsonMapperImpl implements JsonMapper {
    private final ObjectMapper mapper;

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
