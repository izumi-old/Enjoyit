package edu.kozhinov.enjoyit.protocol.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataValidator implements Validator<Data> {
    private final DataTypeValidator typeValidator;
    private final CommandValidator commandValidator;
    private final StatusValidator statusValidator;
    private final ObjectMapper mapper;

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
}
