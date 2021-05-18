package edu.kozhinov.enjoyit.client.ui.console;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.core.async.AsyncComponent;
import edu.kozhinov.enjoyit.protocol.io.Writer;
import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Person;
import edu.kozhinov.enjoyit.core.entity.Room;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Optional;

import static edu.kozhinov.enjoyit.protocol.entity.SimpleRequest.DISCONNECT;
import static edu.kozhinov.enjoyit.protocol.entity.SimpleRequest.GET_ROOMS;
import static edu.kozhinov.enjoyit.protocol.entity.SimpleRequest.LEAVE_THE_ROOM;
import static edu.kozhinov.enjoyit.protocol.entity.SimpleRequest.ROOM_STATUS;

@Slf4j
public class ConsoleAsyncHandler extends AsyncComponent {
    private static final String INPUT_END = "@";
    private final Writer writer;

    public ConsoleAsyncHandler(Writer writer) {
        this.writer = writer;
        log.debug("a console async handler <{}>, <{}> is ready to work", super.hashCode(), hashCode());
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder helper = new StringBuilder();
        try {
            while (!Thread.interrupted()) {
                String line = reader.readLine();
                log.debug("a console async handler <{}> got a new input part from the client", hashCode());
                int indexOf = line.indexOf(INPUT_END);
                if (indexOf == -1) {
                    helper.append(line);
                    helper.append(String.format("%n"));
                } else {
                    helper.append(line, 0, indexOf);
                    String input = helper.toString();
                    helper = new StringBuilder(line.length() > indexOf ? line.substring(indexOf + 1) : "");
                    log.debug("a console async handler <{}> got the end of an input from the client: <{}>",
                            hashCode(), input);
                    handle(input);
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private void handle(String input) {
        if (input.startsWith(Flag.CONNECT_TO_ROOM.asString())) {
            parseRoom(input, Flag.CONNECT_TO_ROOM).ifPresent(room -> writer.write(Command.CONNECT_TO_ROOM, room));
        } else if (input.startsWith(Flag.LOGIN.asString())) {
            parsePerson(input, Flag.LOGIN).ifPresent(person -> writer.write(Command.LOGIN, person));
        } else if (input.startsWith(Flag.REGISTER.asString())) {
            parsePerson(input, Flag.REGISTER).ifPresent(person -> writer.write(Command.REGISTER, person));
        } else if (input.startsWith(Flag.GET_ROOMS.asString())) {
            writer.write(GET_ROOMS);
        } else if (input.startsWith(Flag.LEAVE_THE_ROOM.asString())) {
            writer.write(LEAVE_THE_ROOM);
        } else if (input.startsWith(Flag.CREATE_A_ROOM.asString())) {
            parseRoom(input, Flag.CREATE_A_ROOM).ifPresent(room -> writer.write(Command.CREATE_A_ROOM, room));
        } else if (input.startsWith(Flag.ROOM_STATUS.asString())) {
            writer.write(ROOM_STATUS);
        } else if (input.startsWith(Flag.EXIT.asString())) {
            writer.write(DISCONNECT);
            writer.close();
            System.exit(0);
        } else if (input.startsWith(Flag.HELP.asString())) {
            help();
        } else if (input.startsWith(Flag.FLAG_KEYWORD.asString())) {
            printAllSupportedFlags();
        } else {
            writer.write(Command.MESSAGE, new Message(input, LocalDateTime.now()));
        }
    }

    private void help() {
        System.out.println("\nTo end any message write @");
        System.out.println("If there won't be any correct flag - it will be recognized as a message part.");
        System.out.println("\tCommands list:");
        System.out.println(Flag.HELP + " (without parameters) - to get help");
        System.out.println(Flag.CONNECT_TO_ROOM + " 'ROOM_NAME' 'ROOM_PASSWORD' - to connect to a room. " +
                "password can be not specified if room isn't secured");
        System.out.println(Flag.CREATE_A_ROOM + " 'ROOM_NAME' 'ROOM_PASSWORD' - to create a new room. " +
                "password can be not specified if room won't be secured");
        System.out.println(Flag.LOGIN + " 'USERNAME' 'PASSWORD' - to login into an account");
        System.out.println(Flag.REGISTER + " 'USERNAME' 'PASSWORD' - to create a new account");
        System.out.println(Flag.EXIT + " (without parameters) - to close the chat");
        System.out.println(Flag.GET_ROOMS + " (without parameters) - to get list of all rooms");
        System.out.println(Flag.LEAVE_THE_ROOM + " (without parameters) - to leave current room and enter in general room");
        System.out.println(Flag.ROOM_STATUS + " (without parameters) - to get information about current room and persons list inside the room");
    }

    private void printAllSupportedFlags() {
        System.out.print("This flag keyword is unknown. All supported flags: [");
        Flag[] flags = Flag.values();
        if (flags.length > 1) {
            for (int i = 0; i < flags.length-2; i++) {
                System.out.print(flags[i].asString() + ", ");
            }
            System.out.print(flags[flags.length-2].asString());
        } else {
            System.out.print(flags[0]);
        }
        System.out.println("]");
    }

    private Optional<Room> parseRoom(String input, Flag flag) {
        String[] data = input.substring(input.indexOf(flag.asString()) + 1).split("'");
        if (data.length != 3 && data.length != 5) {
            System.out.printf("Format is incorrect. Please, try again and use such format: %s 'ROOM_NAME' or " +
                            "%s 'ROOM_NAME' 'PASSWORD'%n write --help to get more information%n", flag.asString(),
                    flag.asString());
            return Optional.empty();
        }

        String newRoomName = data[1];
        String newRoomPassword = data.length == 5 ? data[3] : "";
        return Optional.of(new Room(newRoomName, newRoomPassword));
    }

    private Optional<Person> parsePerson(String input, Flag flag) {
        String[] authorities = input.substring(input.indexOf(flag.asString()) + 1).split("'");
        if (authorities.length != 5) {
            System.out.printf("Format is incorrect. Please, try again and use such format: " +
                    "%s 'USERNAME' 'PASSWORD'%n write --help to get more information%n", flag.asString());
            return Optional.empty();
        }

        String username = authorities[1];
        String password = authorities[3];

        return Optional.of(new Person(username, password));
    }
}
