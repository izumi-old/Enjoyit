package edu.kozhinov.enjoyit.protocol.mapper;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.Status;
import edu.kozhinov.enjoyit.protocol.exception.MappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static edu.kozhinov.enjoyit.protocol.Constants.DATA_MESSAGE_DELIMITER;
import static edu.kozhinov.enjoyit.protocol.Constants.DELIMITER;
import static edu.kozhinov.enjoyit.protocol.Constants.EMPTY_JSON;
import static edu.kozhinov.enjoyit.protocol.Constants.EMPTY_STATUS_MESSAGE;
import static edu.kozhinov.enjoyit.protocol.Constants.MESSAGE_POINTER;

@Slf4j
@Component
public class DataMapper implements BiMapper<String, Data> {
    private final BiMapper<String, Data.Type> typeMapper;
    private final BiMapper<String, Command> commandMapper;
    private final BiMapper<String, Status> statusMapper;

    @Autowired
    public DataMapper(BiMapper<String, Data.Type> typeMapper,
                      BiMapper<String, Command> commandMapper,
                      BiMapper<String, Status> statusMapper) {
        this.typeMapper = typeMapper;
        this.commandMapper = commandMapper;
        this.statusMapper = statusMapper;
    }

    @Override
    public Data map1(String s) {
        String[] tmp = s.replace(DATA_MESSAGE_DELIMITER, "").split(DELIMITER);

        if (tmp.length != 5) {
            throw new MappingException(String.format("string is in incorrect format: {%s}. Have to be in format: " +
                    "type(DELIMITER)command(DELIMITER)status_code(DELIMITER)" +
                    "(MESSAGE_POINTER)status_message(DELIMITER)json_body(COMMAND_DELIMITER)", s));
        }

        Data.Type type = typeMapper.map1(tmp[0]);
        Command command = commandMapper.map1(tmp[1]);
        Status status = statusMapper.map1(tmp[2]);
        String message = tmp[3].equals("") ? EMPTY_STATUS_MESSAGE : tmp[3];
        String jsonBody = tmp[4].equals("") ? EMPTY_JSON : tmp[4];

        return new Data(type, command, status, message, jsonBody);
    }

    @Override
    public String map2(Data d) {
        log.debug("mapping a data-message instance: <{}>", d);
        return String.format("%s%s%s%s%s%s%s%s%s%s%s",
                typeMapper.map2(d.getType()), DELIMITER,
                commandMapper.map2(d.getCommand()), DELIMITER,
                statusMapper.map2(d.getStatus()), DELIMITER,
                MESSAGE_POINTER, d.getStatusMessage(), DELIMITER, d.getJsonBody(), DATA_MESSAGE_DELIMITER);
    }
}
