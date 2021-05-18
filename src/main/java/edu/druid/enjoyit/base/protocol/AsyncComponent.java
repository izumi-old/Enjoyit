package edu.druid.enjoyit.base.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

public abstract class AsyncComponent {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());
    private final Thread thread;

    public AsyncComponent(Thread thread) {
        this.thread = thread;
    }

    public void start() {
        if (!thread.isAlive()) {
            thread.start();
            log.debug("an async component <{}> was started", hashCode());
        }
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    public void stop() {
        if (thread.isAlive()) {
            thread.interrupt();
            log.debug("an async component <{}> was stopped", hashCode());
        }
    }
}
