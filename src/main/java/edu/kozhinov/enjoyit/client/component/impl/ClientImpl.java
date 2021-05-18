package edu.kozhinov.enjoyit.client.component.impl;

import edu.kozhinov.enjoyit.core.async.AsyncComponent;
import edu.kozhinov.enjoyit.core.async.AsyncHandlerFactory;
import edu.kozhinov.enjoyit.core.component.JsonMapper;
import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.core.entity.Room;
import edu.kozhinov.enjoyit.client.AsyncMessagesResolver;
import edu.kozhinov.enjoyit.client.component.Client;
import edu.kozhinov.enjoyit.client.ui.Ui;
import edu.kozhinov.enjoyit.client.ui.console.ConsoleAsyncHandler;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.entity.Request;
import edu.kozhinov.enjoyit.protocol.entity.Response;
import edu.kozhinov.enjoyit.protocol.entity.Status;
import edu.kozhinov.enjoyit.protocol.io.WriterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class ClientImpl implements Client {
    private final Socket socket;

    private final AsyncComponent uiInputHandler;
    private final AsyncMessagesResolver messagesResolver;
    private final AsyncComponent asyncHandler;

    private final Ui ui;

    private final JsonMapper jsonMapper;

    private final BlockingQueue<Message> messagesToDisplay = new LinkedBlockingQueue<>();

    @Autowired
    public ClientImpl(Socket socket, WriterFactory writerFactory,
                      Ui ui, JsonMapper jsonMapper, AsyncHandlerFactory asyncHandlerFactory) throws IOException {
        this.socket = socket;
        this.ui = ui;
        this.jsonMapper = jsonMapper;

        this.asyncHandler = asyncHandlerFactory.create(this);
        this.uiInputHandler = new ConsoleAsyncHandler(writerFactory.create(socket));
        this.messagesResolver = new AsyncMessagesResolver(messagesToDisplay, ui);
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        asyncHandler.start();
        uiInputHandler.start();
        messagesResolver.start();
    }

    @Override
    public void handle(Request request) { //some updates from the server probably (f.e. messages from other clients)
        if (request.getCommand() == Command.MESSAGE) {
            Message message = jsonMapper.readValue(request.getJsonBody(), Message.class);
            try {
                messagesToDisplay.put(message);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public void handle(Response response) { //answer to my requests
        Command command = response.getCommand();
        Status status = response.getStatus();
        if (status != Status.OK) {
            ui.displayError(response.getStatusMessage());
            return;
        }

        if (command == Command.GET_ROOMS) {
            ui.displayRooms(Arrays.asList(jsonMapper.readValue(response.getJsonBody(), Room[].class)));
        } else if (command == Command.CONNECT_TO_ROOM) {
            messagesToDisplay.clear();
            ui.displaySuccess("Successfully connected to the room");
        } else if (command == Command.LOGIN) {
            ui.displaySuccess("Login completed successfully");
        } else if (command == Command.REGISTER) {
            ui.displaySuccess("Registration completed successfully. You are already login");
        } else if (command == Command.LEAVE_THE_ROOM) {
            messagesToDisplay.clear();
            ui.displaySuccess("Successfully left the room, now you are in general");
        } else if (command == Command.CREATE_A_ROOM) {
            ui.displaySuccess("The room was created successfully, you can connect it now");
        } else if (command == Command.ROOM_STATUS) {
            ui.displayRoom(jsonMapper.readValue(response.getJsonBody(), Room.class));
        }
    }
}
