package edu.kozhinov.enjoyit.protocol.validator;

import edu.kozhinov.enjoyit.protocol.exception.ValidationException;

public interface Validator<T> {
    void validate(T t) throws ValidationException;
}
