package edu.druid.enjoyit.base.component;

import edu.druid.enjoyit.base.exception.ParseException;

public interface JsonMapper {
    String writeValue(Object o) throws ParseException;
    <T> T readValue(String json, Class<T> clazz) throws ParseException;
}
