package edu.druid.enjoyit.base.protocol.io;

import edu.druid.enjoyit.base.annotation.Factory;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.RequestFactory;
import edu.druid.enjoyit.base.protocol.entity.ResponseFactory;
import edu.druid.enjoyit.base.mapper.BiMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
@Factory
public class WriterFactory {
    private final BiMapper<String, Data> mapper;
    private final RequestFactory requestFactory;
    private final ResponseFactory responseFactory;

    public Writer create(PrintWriter out) {
        return new Writer(out, mapper, requestFactory, responseFactory);
    }

    public Writer create(Socket socket) throws IOException {
        return create(new PrintWriter(socket.getOutputStream(), false));
    }
}
