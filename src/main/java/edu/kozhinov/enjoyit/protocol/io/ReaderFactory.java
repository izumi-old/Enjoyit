package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.mapper.BiMapper;
import edu.kozhinov.enjoyit.protocol.validator.Validator;
import edu.kozhinov.enjoyit.protocol.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@RequiredArgsConstructor
@Component
public class ReaderFactory {
    private final BiMapper<String, Data> mapper;
    private final Validator<Data> validator;

    public Reader create(BufferedReader in) {
        return new Reader(in, mapper, validator);
    }

    public Reader create(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()), Constants.BUFFER_SIZE);
        return create(reader);
    }
}
