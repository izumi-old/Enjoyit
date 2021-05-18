package edu.kozhinov.enjoyit.server.repository;

import edu.kozhinov.enjoyit.base.entity.Room;

import java.util.Optional;

public interface RoomRepository extends SimpleRepository<Room, Long> {
    Optional<Room> findByName(String name);
    Room getGeneral();
}
