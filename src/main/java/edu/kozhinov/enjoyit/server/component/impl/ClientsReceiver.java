package edu.kozhinov.enjoyit.server.component.impl;

import edu.kozhinov.enjoyit.server.component.ClientRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientsReceiver extends Thread {
    private final ServerSocket serverSocket;
    private final ClientRepresentationFactory factory;

    @Setter
    private Consumer<ClientRepresentation> clientConsumer;

    @Override
    public void run() {
        while (!isInterrupted() && !serverSocket.isClosed()) {
            log.debug("Waiting for a new client connection");
            try {
                clientConsumer.accept(factory.create(serverSocket.accept()));
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
