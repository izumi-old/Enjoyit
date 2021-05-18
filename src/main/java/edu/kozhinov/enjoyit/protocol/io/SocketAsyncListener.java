package edu.kozhinov.enjoyit.protocol.io;

import edu.kozhinov.enjoyit.protocol.AsyncComponent;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class SocketAsyncListener extends AsyncComponent {
    public SocketAsyncListener(BlockingQueue<Data> data, Reader reader) {
        super(new Thread(new Listener(data, reader)));
        log.debug("a socket async listener <{}>, <{}> is ready to work", super.hashCode(), hashCode());
    }

    private static final class Listener implements Runnable {
        private final BlockingQueue<Data> data;
        private final Reader reader;

        Listener(BlockingQueue<Data> data, Reader reader) {
            this.data = data;
            this.reader = reader;
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
}
