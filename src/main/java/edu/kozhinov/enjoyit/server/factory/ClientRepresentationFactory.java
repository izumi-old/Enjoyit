package edu.kozhinov.enjoyit.server.factory;

import edu.kozhinov.enjoyit.core.component.JsonMapper;
import edu.kozhinov.enjoyit.protocol.io.ReaderFactory;
import edu.kozhinov.enjoyit.protocol.io.WriterFactory;
import edu.kozhinov.enjoyit.server.component.ClientRepresentation;
import edu.kozhinov.enjoyit.server.component.Server;
import edu.kozhinov.enjoyit.server.component.impl.ClientRepresentationImpl;
import edu.kozhinov.enjoyit.server.repository.MessageRepository;
import edu.kozhinov.enjoyit.server.repository.PersonRepository;
import edu.kozhinov.enjoyit.server.repository.RoomRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
public class ClientRepresentationFactory {
    private final JsonMapper jsonMapper;

    private final Server server;

    private final ReaderFactory readerFactory;
    private final WriterFactory writerFactory;

    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public ClientRepresentationFactory(JsonMapper jsonMapper, @Lazy Server server, ReaderFactory readerFactory,
                                       WriterFactory writerFactory, PersonRepository personRepository,
                                       RoomRepository roomRepository, MessageRepository messageRepository) {
        this.jsonMapper = jsonMapper;
        this.server = server;
        this.readerFactory = readerFactory;
        this.writerFactory = writerFactory;
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    public ClientRepresentation create(Socket socket) throws Exception {
        return new ClientRepresentationImpl(jsonMapper, socket, server, writerFactory, readerFactory,
                personRepository, roomRepository, messageRepository);
    }
}
