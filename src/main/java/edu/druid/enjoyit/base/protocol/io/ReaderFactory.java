package edu.druid.enjoyit.base.protocol.io;

import edu.druid.enjoyit.base.annotation.Factory;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.protocol.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static edu.druid.enjoyit.base.protocol.utils.Constants.BUFFER_SIZE;

@Factory
public class ReaderFactory {
    private final BiMapper<String, Data> mapper;
    private final Validator<Data> validator;

    @Autowired
    public ReaderFactory(BiMapper<String, Data> mapper, Validator<Data> validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    public Reader create(BufferedReader in) {
        return new Reader(in, mapper, validator);
    }

    public Reader create(Socket socket) throws IOException {
        return (create(new BufferedReader(new InputStreamReader(socket.getInputStream()), BUFFER_SIZE)));
    }
}
