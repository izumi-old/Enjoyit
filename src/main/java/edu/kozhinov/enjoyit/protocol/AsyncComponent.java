package edu.kozhinov.enjoyit.protocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AsyncComponent {
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
