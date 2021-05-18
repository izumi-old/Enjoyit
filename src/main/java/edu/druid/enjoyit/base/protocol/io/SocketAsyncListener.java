package edu.druid.enjoyit.base.protocol.io;

import edu.druid.enjoyit.base.protocol.AsyncComponent;
import edu.druid.enjoyit.base.protocol.entity.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

public class SocketAsyncListener extends AsyncComponent {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());

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
