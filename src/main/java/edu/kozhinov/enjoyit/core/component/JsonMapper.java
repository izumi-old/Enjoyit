package edu.kozhinov.enjoyit.core.component;

import edu.kozhinov.enjoyit.core.exception.ParseException;

public interface JsonMapper {
    String writeValue(Object o) throws ParseException;
    <T> T readValue(String json, Class<T> clazz) throws ParseException;
}
