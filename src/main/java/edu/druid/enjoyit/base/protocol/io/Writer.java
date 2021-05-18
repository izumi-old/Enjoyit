package edu.druid.enjoyit.base.protocol.io;

import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.Request;
import edu.druid.enjoyit.base.protocol.entity.RequestFactory;
import edu.druid.enjoyit.base.protocol.entity.Response;
import edu.druid.enjoyit.base.protocol.entity.ResponseFactory;
import edu.druid.enjoyit.base.protocol.entity.SimpleRequest;
import edu.druid.enjoyit.base.protocol.entity.SimpleResponse;
import edu.druid.enjoyit.base.protocol.entity.Status;
import edu.druid.enjoyit.base.mapper.BiMapper;
import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;

@RequiredArgsConstructor
public class Writer {
    private final PrintWriter out;
    private final BiMapper<String, Data> mapper;
    private final RequestFactory requestFactory;
    private final ResponseFactory responseFactory;

    public void write(Command command, Status status) {
        write(responseFactory.create(command, status));
    }

    public void write(Command command, Status status, String statusMessage) {
        write(responseFactory.create(command, status, statusMessage, ""));
    }

    public void write(Command command, Status status, Object body) {
        write(responseFactory.create(command, status, body));
    }

    public void write(Response response) {
        out.println(mapper.map2(response));
        out.flush();
    }

    public void write(SimpleResponse response) {
        write(response.value());
    }

    public void write(Request request) {
        out.println(mapper.map2(request));
        out.flush();
    }

    public void write(Command command, Object body) {
        write(requestFactory.create(command, body));
    }

    public void write(SimpleRequest request) {
        write(request.value());
    }

    public void close() {
        out.flush();
        out.close();
    }
}
