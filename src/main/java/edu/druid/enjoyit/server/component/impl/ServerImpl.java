package edu.druid.enjoyit.server.component.impl;

import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.server.component.ClientRepresentation;
import edu.druid.enjoyit.server.component.Server;
import edu.druid.enjoyit.base.entity.Room;
import edu.druid.enjoyit.server.factory.ClientRepresentationFactory;
import edu.druid.enjoyit.server.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;
import static edu.druid.enjoyit.server.utils.Constants.GENERAL_ROOM_NAME;

@RequiredArgsConstructor
@Component
public class ServerImpl implements Server {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());

    private final ServerSocket serverSocket;
    private final Executor executor;

    private final ClientRepresentationFactory factory;
    private final RoomRepository roomRepository;

    private static final Map<Entity, Collection<ClientRepresentation>> rooms = new ConcurrentHashMap<>();
    private static Collection<ClientRepresentation> clientsInGeneral;
    private static final AtomicBoolean initMarker = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        if (!initMarker.get()) {
            roomRepository.findAll()
                    .forEach(room -> rooms.put(new Entity(room), new LinkedList<>()));
            initMarker.set(true);
        }
    }

    @Override
    public void start() {
        new Thread(() -> {
            log.debug("Server is starting");
            while (!serverSocket.isClosed()) {
                log.debug("Waiting for a new client connection");
                try {
                    add(factory.create(serverSocket.accept()));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void add(ClientRepresentation client) {
        if (clientsInGeneral == null) {
            for (Map.Entry<Entity, Collection<ClientRepresentation>> entry : rooms.entrySet()) {
                if (entry.getKey().room.getName().equals(GENERAL_ROOM_NAME)) {
                    clientsInGeneral = entry.getValue();
                    break;
                }
            }
        }

        clientsInGeneral.add(client);
        executor.execute(client);
        log.debug("A client connected");
    }

    @Override
    public void remove(ClientRepresentation client) {
        getClients(client.getPerson().getCurrentRoom()).remove(client);
    }

    @Override
    public void sendOut(Message message) {
        Room room = message.getRoom();
        getEntity(room).ifPresent(entity -> {
            message.setOrder(entity.messageOrder.getAndIncrement());
            getClients(room).forEach(client -> client.sendToClientApplication(message));
        });
    }

    @Override
    public void addRoom(Room room) {
        rooms.put(new Entity(room), new LinkedList<>());
    }

    @Override
    public void relocate(ClientRepresentation client, Room from, Room to) {
        if (from != null) {
            getClients(from).remove(client);
        }
        getClients(to).add(client);
    }

    private Optional<Entity> getEntity(Room room) {
        for (Map.Entry<Entity, Collection<ClientRepresentation>> entry : rooms.entrySet()) {
            if (entry.getKey().room.equals(room)) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    private Collection<ClientRepresentation> getClients(Room room) {
        for (Map.Entry<Entity, Collection<ClientRepresentation>> entry : rooms.entrySet()) {
            if (entry.getKey().room.equals(room)) {
                return entry.getValue();
            }
        }

        return Collections.emptyList();
    }

    private static final class Entity {
        Room room;
        AtomicLong messageOrder;

        public Entity(Room room) {
            this.room = room;
            this.messageOrder = new AtomicLong(1L);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity entity = (Entity) o;
            return Objects.equals(room, entity.room);
        }

        @Override
        public int hashCode() {
            return Objects.hash(room);
        }
    }
}
