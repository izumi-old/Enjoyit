package edu.kozhinov.enjoyit.client.component.impl;

import edu.kozhinov.enjoyit.base.component.JsonMapper;
import edu.kozhinov.enjoyit.base.entity.Message;
import edu.kozhinov.enjoyit.base.entity.Room;
import edu.kozhinov.enjoyit.client.AsyncMessagesOrderResolver;
import edu.kozhinov.enjoyit.client.component.Client;
import edu.kozhinov.enjoyit.client.ui.Ui;
import edu.kozhinov.enjoyit.client.ui.console.ConsoleAsyncHandler;
import edu.kozhinov.enjoyit.protocol.AsyncComponent;
import edu.kozhinov.enjoyit.protocol.entity.*;
import edu.kozhinov.enjoyit.protocol.io.ReaderFactory;
import edu.kozhinov.enjoyit.protocol.io.SocketAsyncListener;
import edu.kozhinov.enjoyit.protocol.io.Writer;
import edu.kozhinov.enjoyit.protocol.io.WriterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ClientImpl implements Client {
    private final Socket socket;

    private final LinkedBlockingQueue<Data> data = new LinkedBlockingQueue<>();
    private final AsyncComponent serverListener;
    private final AsyncComponent consoleHandler;
    private final AsyncComponent messagesResolver;
    private final Ui ui;

    private final JsonMapper jsonMapper;

    private final BlockingQueue<Message> messagesOrder = new LinkedBlockingQueue<>();
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
        this.messagesResolver = new AsyncMessagesOrderResolver(messagesOrder, ui);
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
                messagesOrder.put(message);
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
            messagesResolver.interrupt();
            serverListener.interrupt();
            consoleHandler.interrupt();
            socket.close();
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }
}
