package edu.kozhinov.enjoyit.server.repository.impl.stub;

import edu.kozhinov.enjoyit.base.entity.Message;
import edu.kozhinov.enjoyit.base.entity.Room;
import edu.kozhinov.enjoyit.server.component.IdGenerator;
import edu.kozhinov.enjoyit.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MessageRepositoryStub implements MessageRepository {
    private static final Map<Long, Message> data = new ConcurrentHashMap<>();
    private final IdGenerator generator;

    @Override
    public <S extends Message> S save(S s) {
        if (s.getId() == null || data.containsKey(s.getId())) {
            s.setId(generator.generate(Message.class));
        }
        data.put(s.getId(), s);
        return (S) data.get(s.getId());
    }

    @Override
    public Optional<Message> findById(Long aLong) {
        Message message = data.get(aLong);
        return message == null ? Optional.empty() : Optional.of(message);
    }

    @Override
    public Iterable<Message> findAll() {
        return data.values();
    }

    @Override
    public Optional<Message> find(Room room, long order) {
        for (Map.Entry<Long, Message> entry : data.entrySet()) {
            Message message = entry.getValue();
            if (message.getOrder() == order && message.getRoom().equals(room)) {
                return Optional.of(message);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Message> findLatest(Room room, int amount) {
        List<Message> messages = data.values().stream()
                .parallel()
                .filter(message -> message.getRoom().equals(room))
                .sorted(Comparator.comparing(Message::getOrder))
                .collect(Collectors.toList());

        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        if (messages.size() < amount) {
            amount = messages.size();
        }

        return messages.subList(messages.size()-amount, messages.size());
    }

    @Override
    public boolean existsById(Long aLong) {
        return data.get(aLong) != null;
    }

    @Override
    public <T extends Message> Optional<T> update(Long aLong, T t) {
        for (Map.Entry<Long, Message> entry : data.entrySet()) {
            Message message = entry.getValue();
            if (message.getClass() == t.getClass() && message.equals(t)) {
                return Optional.of((T) data.put(aLong, message));
            }
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {
        data.remove(aLong);
    }

    @Override
    public void delete(Message message) {
        long id = -1L;
        for (Map.Entry<Long, Message> entry : data.entrySet()) {
            if (entry.getValue().equals(message)) {
                id = entry.getKey();
                break;
            }
        }
        deleteById(id);
    }
}
