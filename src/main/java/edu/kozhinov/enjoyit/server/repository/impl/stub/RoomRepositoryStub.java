package edu.kozhinov.enjoyit.server.repository.impl.stub;

import edu.kozhinov.enjoyit.core.entity.Room;
import edu.kozhinov.enjoyit.server.component.IdGenerator;
import edu.kozhinov.enjoyit.server.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.kozhinov.enjoyit.server.Constants.GENERAL_ROOM_NAME;

@RequiredArgsConstructor
@Repository
public class RoomRepositoryStub implements RoomRepository {
    private static final Map<Long, Room> data = new ConcurrentHashMap<>();
    private static final AtomicBoolean initMarker = new AtomicBoolean(false);

    private final IdGenerator generator;

    private static Room general;

    @PostConstruct
    private void init() {
        if (!initMarker.get()) {
            general = new Room(GENERAL_ROOM_NAME, "");
            save(general);
            initMarker.set(true);
        }
    }

    @Override
    public Room getGeneral() {
        return general;
    }

    @Override
    public <S extends Room> S save(S s) {
        if (s.getId() == null || data.containsKey(s.getId())) {
            s.setId(generator.generate(Room.class));
        }

        data.put(s.getId(), s);
        return (S) data.get(s.getId());
    }

    @Override
    public Optional<Room> findByName(String name) {
        for (Map.Entry<Long, Room> entry : data.entrySet()) {
            Room room = entry.getValue();
            if (room.getName().equals(name)) {
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Room> findById(Long aLong) {
        Room room = data.get(aLong);
        return room == null ? Optional.empty() : Optional.of(room);
    }

    @Override
    public Iterable<Room> findAll() {
        return data.values();
    }

    @Override
    public boolean existsById(Long aLong) {
        return data.get(aLong) != null;
    }

    @Override
    public <T extends Room> Optional<T> update(Long aLong, T t) {
        for (Map.Entry<Long, Room> entry : data.entrySet()) {
            Room room = entry.getValue();
            if (room.getClass() == t.getClass() && room.equals(t)) {
                return Optional.of((T) data.put(aLong, room));
            }
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {
        data.remove(aLong);
    }

    @Override
    public void delete(Room room) {
        long id = -1L;
        for (Map.Entry<Long, Room> entry : data.entrySet()) {
            if (entry.getValue().equals(room)) {
                id = entry.getKey();
                break;
            }
        }
        deleteById(id);
    }
}
