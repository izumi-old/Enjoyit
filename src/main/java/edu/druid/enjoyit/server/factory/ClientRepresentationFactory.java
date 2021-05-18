package edu.druid.enjoyit.server.factory;

import edu.druid.enjoyit.base.annotation.Factory;
import edu.druid.enjoyit.base.component.JsonMapper;
import edu.druid.enjoyit.base.protocol.io.ReaderFactory;
import edu.druid.enjoyit.base.protocol.io.WriterFactory;
import edu.druid.enjoyit.server.component.ClientRepresentation;
import edu.druid.enjoyit.server.component.Server;
import edu.druid.enjoyit.server.component.impl.ClientRepresentationImpl;
import edu.druid.enjoyit.server.repository.MessageRepository;
import edu.druid.enjoyit.server.repository.PersonRepository;
import edu.druid.enjoyit.server.repository.RoomRepository;
import org.springframework.context.annotation.Lazy;

import java.net.Socket;

@Factory
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
