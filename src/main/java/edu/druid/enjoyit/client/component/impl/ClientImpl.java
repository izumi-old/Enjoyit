package edu.druid.enjoyit.client.component.impl;

import edu.druid.enjoyit.base.component.JsonMapper;
import edu.druid.enjoyit.base.protocol.entity.Status;
import edu.druid.enjoyit.base.protocol.io.Writer;
import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Room;
import edu.druid.enjoyit.client.AsyncMessagesOrderResolver;
import edu.druid.enjoyit.client.component.Client;
import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.protocol.entity.Data;
import edu.druid.enjoyit.base.protocol.entity.Request;
import edu.druid.enjoyit.base.protocol.entity.Response;
import edu.druid.enjoyit.base.protocol.AsyncComponent;
import edu.druid.enjoyit.client.ui.Ui;
import edu.druid.enjoyit.client.ui.console.ConsoleAsyncHandler;
import edu.druid.enjoyit.base.protocol.io.SocketAsyncListener;
import edu.druid.enjoyit.base.protocol.io.ReaderFactory;
import edu.druid.enjoyit.base.protocol.io.WriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

@Component
public class ClientImpl implements Client {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());
    private final Socket socket;

    private final LinkedBlockingQueue<Data> data = new LinkedBlockingQueue<>();
    private final AsyncComponent serverListener;
    private final AsyncComponent consoleHandler;
    private final AsyncComponent messagesResolver;
    private final Ui ui;

    private final JsonMapper jsonMapper;

    private final List<Message> messagesOrder = new LinkedList<>();
    private final AtomicBoolean initMarker = new AtomicBoolean(false);

    @Autowired
    public ClientImpl(Socket socket, ReaderFactory readerFactory, WriterFactory writerFactory,
                      Ui ui, JsonMapper jsonMapper) throws IOException {
        Writer writer = writerFactory.create(socket);
        this.socket = socket;
        this.ui = ui;
        this.jsonMapper = jsonMapper;
        this.serverListener = new SocketAsyncListener(data, readerFactory.create(socket));
        this.consoleHandler = new ConsoleAsyncHandler(writer);
        this.messagesResolver = new AsyncMessagesOrderResolver(messagesOrder, ui, writer, initMarker);
    }

    @Override
    public void start() {
        serverListener.start(); //thread #1 - listen data-messages from the server (messages from another clients)
        consoleHandler.start(); //thread #2 - listen user commands via console-UI

        while (!serverListener.isAlive() || !consoleHandler.isAlive()) {
            try {
                Thread.sleep(250L);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }

        Thread handler = new Thread(() -> {
            try {
                while (!socket.isClosed()) { //thread #3 - handle listened data-messages
                    handle(data.take());
                }
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            } finally {
                closeQuietly();
            }
        });
        handler.start();

        while (!handler.isAlive()) {
            try {
                Thread.sleep(250L);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }

        messagesResolver.start(); //thread #4 - resolve which messages possible to display now and ask for missed
    }

    private void handle(Data data) { //resolve what dealing with
        if (data.getType() == Data.Type.REQUEST) {
            handle(Request.convert(data));
        } else if (data.getType() == Data.Type.RESPONSE) {
            handle(Response.convert(data));
        }
    }

    private void handle(Request request) { //some updates from the server probably (f.e. messages from other clients)
        if (request.getCommand() == Command.MESSAGE) {
            Message message = jsonMapper.readValue(request.getJsonBody(), Message.class);
            try {
                synchronized (messagesOrder) {
                    messagesOrder.add(message);
                    messagesOrder.notifyAll();
                    messagesOrder.wait();
                }
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private void handle(Response response) { //answer to my requests
        Command command = response.getCommand();
        Status status = response.getStatus();
        if (status != Status.OK) {
            ui.displayError(response.getStatusMessage());
            return;
        }

        if (command == Command.GET_ROOMS) {
            ui.displayRooms(Arrays.asList(jsonMapper.readValue(response.getJsonBody(), Room[].class)));
        } else if (command == Command.CONNECT_TO_ROOM) {
            synchronized (messagesOrder) {
                messagesOrder.clear();
                initMarker.set(false);
            }
            ui.displaySuccess("Successfully connected to the room");
        } else if (command == Command.LOGIN) {
            ui.displaySuccess("Login completed successfully");
        } else if (command == Command.REGISTER) {
            ui.displaySuccess("Registration completed successfully. You are already login");
        } else if (command == Command.LEAVE_THE_ROOM) {
            synchronized (messagesOrder) {
                messagesOrder.clear();
                initMarker.set(false);
            }
            ui.displaySuccess("Successfully left the room, now you are in general");
        } else if (command == Command.CREATE_A_ROOM) {
            ui.displaySuccess("The room was created successfully, you can connect it now");
        } else if (command == Command.ROOM_STATUS) {
            ui.displayRoom(jsonMapper.readValue(response.getJsonBody(), Room.class));
        }
    }

    private void closeQuietly() {
        try {
            messagesResolver.stop();
            serverListener.stop();
            consoleHandler.stop();
            socket.close();
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }
}
