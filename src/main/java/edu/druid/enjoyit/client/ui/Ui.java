package edu.druid.enjoyit.client.ui;

import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Room;

import java.util.Collection;

public interface Ui {
    void displayRoom(Room room);
    void displayRooms(Collection<Room> rooms);
    void displayNewMessage(Message message);

    void displayError(String error);
    void displaySuccess(String success);
}
