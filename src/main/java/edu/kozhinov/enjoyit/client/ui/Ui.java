package edu.kozhinov.enjoyit.client.ui;

import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Room;

import java.util.Collection;

public interface Ui {
    void displayRoom(Room room);
    void displayRooms(Collection<Room> rooms);
    void displayNewMessage(Message message);

    void displayError(String error);
    void displaySuccess(String success);
}
