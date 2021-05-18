package edu.druid.enjoyit.base.protocol.mapper;

import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.annotation.Mapper;
import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.Status;
import edu.druid.enjoyit.base.protocol.exception.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static edu.druid.enjoyit.base.protocol.utils.Constants.DATA_MESSAGE_DELIMITER;
import static edu.druid.enjoyit.base.protocol.utils.Constants.DELIMITER;
import static edu.druid.enjoyit.base.protocol.utils.Constants.EMPTY_JSON;
import static edu.druid.enjoyit.base.protocol.utils.Constants.EMPTY_STATUS_MESSAGE;
import static edu.druid.enjoyit.base.protocol.utils.Constants.MESSAGE_POINTER;
import static edu.druid.enjoyit.base.protocol.utils.Utils.decode;
import static edu.druid.enjoyit.base.protocol.utils.Utils.encode;
import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

@Mapper
public class DataMapper implements BiMapper<String, Data> {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());
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
        s = decode(s);
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
        return encode(String.format("%s%s%s%s%s%s%s%s%s%s%s",
                typeMapper.map2(d.getType()), DELIMITER,
                commandMapper.map2(d.getCommand()), DELIMITER,
                statusMapper.map2(d.getStatus()), DELIMITER,
                MESSAGE_POINTER, d.getStatusMessage(), DELIMITER, d.getJsonBody(), DATA_MESSAGE_DELIMITER));
    }
}
