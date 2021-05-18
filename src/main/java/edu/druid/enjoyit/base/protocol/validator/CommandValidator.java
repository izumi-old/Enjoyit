package edu.druid.enjoyit.base.protocol.validator;

import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.exception.ValidationException;

@edu.druid.enjoyit.base.annotation.Validator
public class CommandValidator implements Validator<Command> {
    @Override
    public void validate(Command command) throws ValidationException {
        if (command == null) {
            throw new ValidationException("command is null");
        }
    }
}
