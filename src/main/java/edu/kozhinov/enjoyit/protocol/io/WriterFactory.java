package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.RequestFactory;
import edu.kozhinov.enjoyit.protocol.entity.ResponseFactory;
import edu.kozhinov.enjoyit.protocol.mapper.BiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
@Component
public class WriterFactory {
    private final BiMapper<String, Data> mapper;
    private final RequestFactory requestFactory;
    private final ResponseFactory responseFactory;

    public Writer create(PrintWriter out) {
        return new Writer(out, mapper, requestFactory, responseFactory);
    }

    public Writer create(Socket socket) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
        return create(printWriter);
    }
}
