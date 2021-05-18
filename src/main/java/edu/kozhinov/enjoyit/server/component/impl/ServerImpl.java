package edu.kozhinov.enjoyit.server.component.impl;

import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.server.component.ClientRepresentation;
import edu.kozhinov.enjoyit.server.component.Server;
import edu.kozhinov.enjoyit.core.entity.Room;
import edu.kozhinov.enjoyit.server.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

import static edu.kozhinov.enjoyit.server.utils.Constants.GENERAL_ROOM_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class ServerImpl implements Server {
    private final ClientsReceiver clientsReceiver;

    private final Executor executor;

    private final RoomRepository roomRepository;

    private static final Map<Entity, Collection<ClientRepresentation>> rooms = new ConcurrentHashMap<>();
    private static Collection<ClientRepresentation> clientsInGeneral;
    private static final AtomicBoolean initMarker = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        if (!initMarker.get()) {
            roomRepository.findAll()
                .forEach(room -> {
                    Collection<ClientRepresentation> clientsInRoom = new LinkedList<>();
                    rooms.put(new Entity(room), clientsInRoom);

                    if (room.getName().equals(GENERAL_ROOM_NAME)) {
                        clientsInGeneral = clientsInRoom;
                    }
                });
            initMarker.set(true);
        }
    }

    @Override
    public void start() {
        log.debug("Server is starting");
        clientsReceiver.setClientConsumer(this::add);
        clientsReceiver.start();
    }

    @Override
    public void add(ClientRepresentation client) {
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
        log.debug("Creating a new one room ({})", room.getName());
        rooms.put(new Entity(room), new LinkedList<>());
        roomRepository.save(room);
    }

    @Override
    public void relocate(ClientRepresentation client, Room from, Room to) {
        log.debug("Relocating a client. Username: {}", client.getPerson().getUsername());
        if (from != null) {
            log.debug("Removing the client from the old room ({})", from.getName());
            getClients(from).remove(client);
        }
        log.debug("Adding the client to the new room ({})", to.getName());
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
