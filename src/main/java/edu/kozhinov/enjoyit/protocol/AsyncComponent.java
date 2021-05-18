package edu.kozhinov.enjoyit.protocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AsyncComponent extends Thread {

    @Override
    public void start() {
        log.debug("an async component <{}> was started", hashCode());
        super.start();
    }
}
