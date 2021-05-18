package edu.kozhinov.enjoyit.core.async;

import edu.kozhinov.enjoyit.core.exception.EnjoyitException;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
@Slf4j
public class AsyncDataResolver extends AsyncComponent {
    private final BlockingQueue<Data> dataQueue;
    private final Asynchronous asynchronous;

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Data data = dataQueue.take();
                if (data.getType() == Data.Type.REQUEST) {
                    asynchronous.handle(Request.convert(data));
                } else if (data.getType() == Data.Type.RESPONSE) {
                    asynchronous.handle(Response.convert(data));
                } else {
                    throw new EnjoyitException("Unknown protocol data type. " + data);
                }
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
