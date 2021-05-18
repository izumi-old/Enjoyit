package edu.druid.enjoyit.base.protocol.validator;

import edu.druid.enjoyit.base.protocol.entity.Status;
import edu.druid.enjoyit.base.exception.ValidationException;

@edu.druid.enjoyit.base.annotation.Validator
public class StatusValidator implements Validator<Status> {
    @Override
    public void validate(Status status) throws ValidationException {
        if (status == null) {
            throw new ValidationException("status is null");
        }
    }
}
