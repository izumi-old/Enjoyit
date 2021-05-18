package edu.druid.enjoyit.server.repository;

import edu.druid.enjoyit.base.entity.Room;

import java.util.Optional;

public interface RoomRepository extends SimpleRepository<Room, Long> {
    Optional<Room> findByName(String name);
    Room getGeneral();
}
