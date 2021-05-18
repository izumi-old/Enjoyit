package edu.druid.enjoyit.base.protocol.io;

import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.Request;
import edu.druid.enjoyit.base.protocol.entity.Response;
import edu.druid.enjoyit.base.protocol.exception.ConversionException;
import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.protocol.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

import static edu.druid.enjoyit.base.protocol.utils.Constants.DATA_MESSAGE_DELIMITER;
import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

public class Reader implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());
    private final BufferedReader in;
    private String left = "";

    private final BiMapper<String, Data> mapper;
    private final Validator<Data> validator;

    Reader(BufferedReader in, BiMapper<String, Data> mapper, Validator<Data> validator) {
        this.in = in;
        this.mapper = mapper;
        this.validator = validator;
    }

    public Data read() throws IOException {
        StringBuilder helper = new StringBuilder(left);
        left = "";
        while (true) {
            String line = in.readLine();
            int indexOfCommandDelimiter = line.indexOf(DATA_MESSAGE_DELIMITER);
            if (indexOfCommandDelimiter == -1) {
                helper.append(line);
            } else {
                helper.append(line, 0, indexOfCommandDelimiter);
                left = line.substring(indexOfCommandDelimiter + DATA_MESSAGE_DELIMITER.length());
                log.debug("Got a data-message, raw: <{}>", helper.toString());
                Data data = mapper.map1(helper.toString());
                log.debug("The data-message was successfully parsed");
                validator.validate(data);
                log.debug("The data-message was validated. everything is ok");
                return data;
            }
        }
    }

    public <T extends Data> T read(Class<T> clazz) throws IOException {
        Data data = read();
        if (clazz == Response.class) {
            return (T) Response.convert(data);
        } else if (clazz == Request.class) {
            return (T) Request.convert(data);
        } else {
            throw new ConversionException(clazz.getName() + " is an unknown type of data-messages");
        }
    }

    public boolean ready() throws IOException {
        return in.ready();
    }

    public void close() throws IOException {
        in.close();
    }
}
