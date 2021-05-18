package edu.druid.enjoyit.client.ui.console;

import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Person;
import edu.druid.enjoyit.base.entity.Room;
import edu.druid.enjoyit.client.ui.Ui;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Component
public class ConsoleUi implements Ui {
    @Override
    public void displayRoom(Room room) {
        StringBuilder result = new StringBuilder(String.format("%nSYSTEM INFO:"));
        result.append('\t');
        result.append(String.format("Current room info:%n"));
        result.append('\t');
        result.append('\t');
        result.append(String.format("Name: %s%n", room.getName()));
        result.append('\t');
        result.append('\t');
        result.append(String.format("Password: %s%n", room.isSecured() ? room.getPassword() : "[WITHOUT PASSWORD]"));
        result.append('\t');
        result.append(String.format("Persons in the room:%n"));
        room.getPersons().forEach(person -> {
            result.append('\t');
            result.append('\t');
            result.append(String.format("Username: %s%n", person.getUsername()));
        });
        System.out.print(result.toString());
    }

    @Override
    public void displayRooms(Collection<Room> rooms) {
        StringBuilder result = new StringBuilder(String.format("%nSYSTEM INFO:"));
        result.append('\t');
        result.append(String.format("List of rooms:%n"));
        rooms.forEach(room -> {
            result.append('\t');
            result.append(room.getName());
            result.append(" (");
            result.append(room.isSecured() ? "secured by password" : "not secured by password");
            result.append(')');
            result.append(String.format("%n"));
        });
        System.out.print(result.toString());
    }

    @Override
    public void displayNewMessage(Message message) {
        Person sender = message.getSender();
        System.out.printf("(%s) %s: %s%n",
                message.getCreationTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                sender.getUsername(), message.getBody());
    }

    @Override
    public void displayError(String error) {
        System.out.printf("    SYSTEM INFO. An error occurred: %s%n", error);
    }

    @Override
    public void displaySuccess(String success) {
        System.out.printf("    SYSTEM INFO: %s%n", success);
    }
}
