package edu.kozhinov.enjoyit.protocol.validator;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CommandValidator implements Validator<Command> {
    @Override
    public void validate(Command command) throws ValidationException {
        if (command == null) {
            throw new ValidationException("command is null");
        }
    }
}
