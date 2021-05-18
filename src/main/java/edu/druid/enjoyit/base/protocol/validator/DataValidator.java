package edu.druid.enjoyit.base.protocol.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

@edu.druid.enjoyit.base.annotation.Validator
public class DataValidator implements Validator<Data> {
    private DataTypeValidator typeValidator;
    private CommandValidator commandValidator;
    private StatusValidator statusValidator;

    private ObjectMapper mapper;

    @Override
    public void validate(Data data) throws ValidationException {
        typeValidator.validate(data.getType());
        commandValidator.validate(data.getCommand());
        statusValidator.validate(data.getStatus());

        if (data.getStatusMessage() == null) {
            throw new ValidationException("message' status-message is null");
        }

        if (data.getJsonBody() == null) {
            throw new ValidationException("message' json body is null");
        }

        try {
            mapper.readTree(data.getJsonBody());
        } catch (JsonProcessingException ex) {
            throw new ValidationException("message' json body isn't JSON", ex);
        }
    }

    @Autowired
    public void setCommandValidator(CommandValidator commandValidator) {
        this.commandValidator = commandValidator;
    }

    @Autowired
    public void setStatusValidator(StatusValidator statusValidator) {
        this.statusValidator = statusValidator;
    }

    @Autowired
    public void setTypeValidator(DataTypeValidator typeValidator) {
        this.typeValidator = typeValidator;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
