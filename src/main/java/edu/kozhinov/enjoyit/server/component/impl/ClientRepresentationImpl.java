package edu.kozhinov.enjoyit.server.component.impl;

import edu.kozhinov.enjoyit.core.async.*;
import edu.kozhinov.enjoyit.core.component.JsonMapper;
import edu.kozhinov.enjoyit.protocol.entity.Status;
import edu.kozhinov.enjoyit.core.entity.Room;
import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Person;
import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.entity.Data;
import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.Response;
import edu.kozhinov.enjoyit.protocol.io.Writer;
import edu.kozhinov.enjoyit.protocol.io.WriterFactory;
import edu.kozhinov.enjoyit.server.component.ClientRepresentation;
import edu.kozhinov.enjoyit.server.component.Server;
import edu.kozhinov.enjoyit.server.repository.MessageRepository;
import edu.kozhinov.enjoyit.server.repository.PersonRepository;
import edu.kozhinov.enjoyit.server.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class ClientRepresentationImpl implements ClientRepresentation {
    private final JsonMapper jsonMapper;

    private final Socket socket;
    private final Server server;

    private final Writer writer;

    private final AsyncHandler asyncHandler;

    private Person person;

    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public ClientRepresentationImpl(JsonMapper jsonMapper, Socket socket, Server server, WriterFactory writerFactory,
                                    PersonRepository personRepository, RoomRepository roomRepository,
                                    MessageRepository messageRepository, AsyncHandlerFactory asyncHandlerFactory) throws IOException {
        this.jsonMapper = jsonMapper;

        this.socket = socket;
        this.server = server;

        this.writer = writerFactory.create(socket);

        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;

        this.asyncHandler = asyncHandlerFactory.create(this);
    }

    @Override
    public void run() {
        asyncHandler.start();
        person = new Person();
        relocate(roomRepository.getGeneral());
        sendConnect();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void sendToClientApplication(Message message) {
        writer.write(Command.MESSAGE, message);
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public void handle(Request request) { //client asks to do some action
        Command command = request.getCommand();
        if (command == Command.MESSAGE) {
            sendMessage(request);
        } else if (command == Command.GET_ROOMS) {
            getRooms();
        } else if (command == Command.CONNECT_TO_ROOM) {
            enterRoom(request);
        } else if (command == Command.LOGIN) {
            login(request);
        } else if (command == Command.REGISTER) {
            register(request);
        } else if (command == Command.LEAVE_THE_ROOM) {
            exitRoom();
        } else if (command == Command.CREATE_A_ROOM) {
            createRoom(request);
        } else if (command == Command.ROOM_STATUS) {
            roomStatus();
        } else if (command == Command.MESSAGE_LOST) {
            messageLost(request);
        } else if (command == Command.DISCONNECT) {
            closeQuietly();
        }
    }

    @Override
    public void handle(Response response) { //client got server updates (got another clients messages)
        if (response.getCommand().equals(Command.MESSAGE_LOST)) {
            messageLost(response);
        } else {
            log.warn("Some strange behaviour was traced. Client sent a response, but it's unknown situation. " +
                    "Response: <{}>", response);
        }
    }

    private void sendMessage(Request request) {
        Message message = jsonMapper.readValue(request.getJsonBody(), Message.class);
        message.setSender(person);
        message.setRoom(person.getCurrentRoom());

        server.sendOut(message);
        messageRepository.save(message);
    }

    private void getRooms() {
        writer.write(Command.GET_ROOMS, Status.OK, roomRepository.findAll());
    }

    private void enterRoom(Request request) {
        Command command = request.getCommand();
        Room room = jsonMapper.readValue(request.getJsonBody(), Room.class);
        Optional<Room> optional = roomRepository.findByName(room.getName());
        if (optional.isPresent()) {
            Room wanted = optional.get();
            if (wanted.fit(room.getPassword())) {
                sendDisconnect();
                relocate(wanted);
                sendConnect();
                writer.write(command, Status.OK);
            } else {
                writer.write(command, Status.BAD_REQUEST, "Provided password doesn't fit");
            }
        } else {
            writer.write(command, Status.BAD_REQUEST, "No any room with such name");
        }
    }

    private void login(Request request) {
        Command command = request.getCommand();
        Person person = jsonMapper.readValue(request.getJsonBody(), Person.class);
        Optional<Person> optional = personRepository.findByUsername(person.getUsername());
        if (optional.isPresent()) {
            Person wanted = optional.get();
            if (wanted.fit(person.getPassword())) {
                Room room = this.person.getCurrentRoom();
                room.getPersons().remove(this.person);
                this.person = wanted;
                this.person.setCurrentRoom(room);
                room.getPersons().add(this.person);
                writer.write(command, Status.OK);
            } else {
                writer.write(command, Status.BAD_REQUEST, "Incorrect username or password");
            }
        } else {
            writer.write(command, Status.BAD_REQUEST, "Incorrect username or password");
        }
    }

    private void register(Request request) {
        Person person = jsonMapper.readValue(request.getJsonBody(), Person.class);
        person.setCurrentRoom(this.person.getCurrentRoom());
        Person saved = personRepository.save(person);
        this.person = saved;
        writer.write(Command.REGISTER, Status.OK, saved);
    }

    private void exitRoom() {
        sendDisconnect();
        relocate(roomRepository.getGeneral());
        sendConnect();
        writer.write(Command.LEAVE_THE_ROOM, Status.OK);
    }

    private void sendDisconnect() {
        Message messageDisconnect = new Message(person, "Disconnected this room", LocalDateTime.now());
        messageDisconnect.setRoom(person.getCurrentRoom());
        server.sendOut(messageDisconnect);
        messageRepository.save(messageDisconnect);
    }

    private void relocate(Room wanted) {
        Room old = person.getCurrentRoom();
        if (old != null) {
            old.getPersons().remove(person);
        }
        server.relocate(this, old, wanted);
        person.setCurrentRoom(wanted);
        person.getCurrentRoom().getPersons().add(person);
        if (!person.isAnonymous()) {
            personRepository.update(person.getId(), person);
        }

        messageRepository.findLatest(wanted, 10)
                .forEach(this::sendToClientApplication);
    }

    private void sendConnect() {
        Message messageConnect = new Message(person, "Connected to this room", LocalDateTime.now());
        messageConnect.setRoom(person.getCurrentRoom());
        server.sendOut(messageConnect);
        messageRepository.save(messageConnect);
    }

    private void createRoom(Request request) {
        Room room = jsonMapper.readValue(request.getJsonBody(), Room.class);
        Room saved = roomRepository.save(room);
        server.addRoom(room);
        writer.write(Command.CREATE_A_ROOM, Status.OK, saved);
    }

    private void roomStatus() {
        writer.write(Command.ROOM_STATUS, Status.OK, person.getCurrentRoom());
    }

    private void messageLost(Data data) {
        Long[] orders = jsonMapper.readValue(data.getJsonBody(), Long[].class);
        Arrays.stream(orders)
                .forEach(order -> {
                    log.debug("client asked missed message with order <{}> in room <{}>", order,
                            person.getCurrentRoom());
                    Optional<Message> optional = messageRepository.find(person.getCurrentRoom(), order);
                    if (optional.isPresent()) {
                        log.debug("the message was found: <{}>", optional.get());
                    } else {
                        log.debug("the message wasn't found");
                    }
                    optional.ifPresent(this::sendToClientApplication);
                });
    }

    private void closeQuietly() {
        try {
            person.getCurrentRoom().getPersons().remove(person);
            server.remove(this);
            asyncHandler.interrupt();
            writer.close();
            socket.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
