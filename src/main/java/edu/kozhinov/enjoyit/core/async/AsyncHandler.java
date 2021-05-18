package edu.kozhinov.enjoyit.core.async;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AsyncHandler extends AsyncComponent {
    private final AsyncSocketListener asyncListener;
    private final AsyncDataResolver asyncDataResolver;

    @Override
    public void run() {
        asyncListener.start();
        asyncDataResolver.start();
    }

    @Override
    public void interrupt() {
        asyncListener.interrupt();
        asyncDataResolver.interrupt();
    }
}
