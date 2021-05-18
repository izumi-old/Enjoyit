package edu.druid.enjoyit.base.protocol.validator;

import edu.druid.enjoyit.base.exception.ValidationException;

public interface Validator<T> {
    void validate(T t) throws ValidationException;
}
