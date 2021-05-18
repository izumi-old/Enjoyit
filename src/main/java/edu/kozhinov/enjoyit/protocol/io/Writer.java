package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.RequestFactory;
import edu.kozhinov.enjoyit.protocol.entity.Response;
import edu.kozhinov.enjoyit.protocol.entity.ResponseFactory;
import edu.kozhinov.enjoyit.protocol.entity.SimpleRequest;
import edu.kozhinov.enjoyit.protocol.entity.SimpleResponse;
import edu.kozhinov.enjoyit.protocol.entity.Status;
import edu.kozhinov.enjoyit.protocol.mapper.BiMapper;
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
