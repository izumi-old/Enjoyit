package edu.kozhinov.enjoyit.server.repository;

import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Room;

import java.util.Collection;
import java.util.Optional;

public interface MessageRepository extends SimpleRepository<Message, Long> {
    Optional<Message> find(Room room, long order);
    Collection<Message> findLatest(Room room, int amount);
}
