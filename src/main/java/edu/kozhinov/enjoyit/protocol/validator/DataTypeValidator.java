package edu.kozhinov.enjoyit.protocol.validator;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class DataTypeValidator implements Validator<Data.Type>{
    @Override
    public void validate(Data.Type type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("type is null");
        }
    }
}
