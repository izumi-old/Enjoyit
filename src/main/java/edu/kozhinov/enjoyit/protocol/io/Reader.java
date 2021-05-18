package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.Response;
import edu.kozhinov.enjoyit.protocol.exception.ConversionException;
import edu.kozhinov.enjoyit.protocol.mapper.BiMapper;
import edu.kozhinov.enjoyit.protocol.validator.Validator;
import edu.kozhinov.enjoyit.protocol.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class Reader implements AutoCloseable {
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
            int indexOfCommandDelimiter = line.indexOf(Constants.DATA_MESSAGE_DELIMITER);
            if (indexOfCommandDelimiter == -1) {
                helper.append(line);
            } else {
                helper.append(line, 0, indexOfCommandDelimiter);
                left = line.substring(indexOfCommandDelimiter + Constants.DATA_MESSAGE_DELIMITER.length());
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
