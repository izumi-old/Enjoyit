package edu.kozhinov.enjoyit.server.component;

import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Room;

public interface Server {
    void start();
    void add(ClientRepresentation client);
    void remove(ClientRepresentation client);

    void sendOut(Message message);

    void addRoom(Room room);
    void relocate(ClientRepresentation client, Room from, Room to);
}
