package edu.kozhinov.enjoyit.core.async;

import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.Response;

public interface Asynchronous {
    void handle(Request request);
    void handle(Response response);
}
