package edu.kozhinov.enjoyit.protocol.validator;

import edu.kozhinov.enjoyit.protocol.entity.Status;
import edu.kozhinov.enjoyit.protocol.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class StatusValidator implements Validator<Status> {

    @Override
    public void validate(Status status) throws ValidationException {
        if (status == null) {
            throw new ValidationException("status is null");
        }
    }
}
