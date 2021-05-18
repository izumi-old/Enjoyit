package edu.kozhinov.enjoyit.core.async;

import edu.kozhinov.enjoyit.core.AsyncSocketHolder;
import edu.kozhinov.enjoyit.core.exception.EnjoyitException;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.io.ReaderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor
@Component
public class AsyncHandlerFactory {
    private final ReaderFactory readerFactory;

    public AsyncHandler create(AsyncSocketHolder holder) {
        try {
            BlockingQueue<Data> data = new LinkedBlockingQueue<>();
            return new AsyncHandler(
                    new AsyncSocketListener(data, readerFactory.create(holder.getSocket())),
                    new AsyncDataResolver(data, holder));
        } catch (IOException ex) {
            throw new EnjoyitException(ex);
        }
    }
}
