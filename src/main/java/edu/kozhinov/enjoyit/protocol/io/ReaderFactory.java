package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.mapper.BiMapper;
import edu.kozhinov.enjoyit.protocol.validator.Validator;
import edu.kozhinov.enjoyit.protocol.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Component
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
        return (create(new BufferedReader(new InputStreamReader(socket.getInputStream()), Constants.BUFFER_SIZE)));
    }
}
