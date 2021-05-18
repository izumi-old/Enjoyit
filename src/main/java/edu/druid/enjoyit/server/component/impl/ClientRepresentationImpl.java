package edu.druid.enjoyit.server.component.impl;

import edu.druid.enjoyit.base.component.JsonMapper;
import edu.druid.enjoyit.base.protocol.AsyncComponent;
import edu.druid.enjoyit.base.protocol.entity.Status;
import edu.druid.enjoyit.base.entity.Room;
import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Person;
import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.Request;
import edu.druid.enjoyit.base.protocol.entity.Response;
import edu.druid.enjoyit.base.protocol.io.ReaderFactory;
import edu.druid.enjoyit.base.protocol.io.SocketAsyncListener;
import edu.druid.enjoyit.base.protocol.io.Writer;
import edu.druid.enjoyit.base.protocol.io.WriterFactory;
import edu.druid.enjoyit.server.component.ClientRepresentation;
import edu.druid.enjoyit.server.component.Server;
import edu.druid.enjoyit.server.repository.MessageRepository;
import edu.druid.enjoyit.server.repository.PersonRepository;
import edu.druid.enjoyit.server.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

public class ClientRepresentationImpl implements ClientRepresentation {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());

    private final JsonMapper jsonMapper;

    private final Socket socket;
    private final Server server;

    private final Writer writer;

    private final LinkedBlockingQueue<Data> data = new LinkedBlockingQueue<>();
    private final AsyncComponent asyncListener;

    private Person person;

    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public ClientRepresentationImpl(JsonMapper jsonMapper, Socket socket, Server server, WriterFactory writerFactory,
                                    ReaderFactory readerFactory, PersonRepository personRepository,
                                    RoomRepository roomRepository, MessageRepository messageRepository) throws IOException {
        this.jsonMapper = jsonMapper;

        this.socket = socket;
        this.server = server;

        this.asyncListener = new SocketAsyncListener(data, readerFactory.create(socket));
        this.writer = writerFactory.create(socket);

        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run() {
        asyncListener.start(); //thread #1 to listen data-messages from client

        while (!asyncListener.isAlive()) {
            try {
                Thread.sleep(250L);
            } catch (InterruptedException ignored) {}
        }

        person = new Person(); //while user not login server recognizes him as an anonymous
        relocate(roomRepository.getGeneral());
        sendConnect();
        new Thread(this::handler).start(); //thread #2 to handle data-messages from client
    }

    @Override
    public void sendToClientApplication(Message message) { //thread #3 to send messages from other clients asynchronous
        writer.write(Command.MESSAGE, message);
    }

    @Override
    public Person getPerson() {
        return person;
    }

    private void handle(Data data) { //resolve what dealing with
        if (data.getType() == Data.Type.REQUEST) {
            handle(Request.convert(data));
        } else if (data.getType() == Data.Type.RESPONSE) {
            handle(Response.convert(data));
        }
    }

    private void handle(Request request) { //client asks to do some action
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
        //todo: validate
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
        //todo: validate
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
        //todo: validate
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
        //todo: validation; room.name have to be unique
        Room saved = roomRepository.save(room);
        server.addRoom(room);
        writer.write(Command.CREATE_A_ROOM, Status.OK, saved);
    }

    private void roomStatus() {
        writer.write(Command.ROOM_STATUS, Status.OK, person.getCurrentRoom());
    }

    private void handle(Response response) { //client got server updates (got another clients messages)
        if (response.getCommand().equals(Command.MESSAGE_LOST)) { //todo: what will happens if user left room where he was?
            messageLost(response);
        } else {
            log.warn("Some strange behaviour was traced. Client sent a response, but it's unknown situation. " +
                            "Response: <{}>", response);
        }
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

    private void handler() {
        synchronized (data) {
            try {
                while (!socket.isClosed()) {
                    handle(data.take());
                }
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            } finally {
                closeQuietly();
            }
        }
    }

    private void closeQuietly() {
        try {
            person.getCurrentRoom().getPersons().remove(person);
            asyncListener.stop();
            server.remove(this);
            writer.close();
            socket.close();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }
}