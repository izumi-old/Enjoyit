package edu.druid.enjoyit.server.repository;

import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Room;

import java.util.Collection;
import java.util.Optional;

public interface MessageRepository extends SimpleRepository<Message, Long> {
    Optional<Message> find(Room room, long order);
    Collection<Message> findLatest(Room room, int amount);
}
