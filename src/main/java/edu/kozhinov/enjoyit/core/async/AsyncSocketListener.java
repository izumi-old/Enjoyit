package edu.kozhinov.enjoyit.core.async;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.io.Reader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class AsyncSocketListener extends AsyncComponent {
    private final BlockingQueue<Data> data;
    private final Reader reader;

    public AsyncSocketListener(BlockingQueue<Data> data, Reader reader) {
        this.data = data;
        this.reader = reader;
        log.debug("a socket async listener <{}>, <{}> is ready to work", super.hashCode(), hashCode());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                data.add(reader.read());
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
