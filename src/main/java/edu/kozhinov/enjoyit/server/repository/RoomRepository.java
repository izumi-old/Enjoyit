package edu.kozhinov.enjoyit.server.repository;

import edu.kozhinov.enjoyit.core.entity.Room;

import java.util.Optional;

public interface RoomRepository extends SimpleRepository<Room, Long> {
    Optional<Room> findByName(String name);
    Room getGeneral();
}
