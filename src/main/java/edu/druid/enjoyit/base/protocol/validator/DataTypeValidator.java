package edu.druid.enjoyit.base.protocol.validator;

import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.exception.ValidationException;

@edu.druid.enjoyit.base.annotation.Validator
public class DataTypeValidator implements Validator<Data.Type>{
    @Override
    public void validate(Data.Type type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("type is null");
        }
    }
}
