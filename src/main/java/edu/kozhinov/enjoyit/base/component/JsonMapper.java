package edu.kozhinov.enjoyit.base.component;

import edu.kozhinov.enjoyit.base.exception.ParseException;

public interface JsonMapper {
    String writeValue(Object o) throws ParseException;
    <T> T readValue(String json, Class<T> clazz) throws ParseException;
}
