package edu.kozhinov.enjoyit.client;

import edu.kozhinov.enjoyit.base.entity.Message;
import edu.kozhinov.enjoyit.client.ui.Ui;
import edu.kozhinov.enjoyit.protocol.AsyncComponent;
import edu.kozhinov.enjoyit.protocol.entity.Command;
import edu.kozhinov.enjoyit.protocol.io.Writer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class AsyncMessagesOrderResolver extends AsyncComponent {
    private final Collection<Message> messages;
    private final Ui ui;
    private final Writer writer;
    private final AtomicBoolean initMarker;

    private long messageOrder;

    public AsyncMessagesOrderResolver(Collection<Message> messages, Ui ui, Writer writer, AtomicBoolean initMarker) {
        this.messages = messages;
        this.ui = ui;
        this.writer = writer;
        this.initMarker = initMarker;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (messages) {
                    if (messages.isEmpty()) {
                        messages.notifyAll();
                        messages.wait();
                    }

                    if (!initMarker.get()) {
                        long min = Long.MAX_VALUE;
                        for (Message message : messages) {
                            if (message.getOrder() < min) {
                                min = message.getOrder();
                            }
                        }
                        messageOrder = min;
                        initMarker.set(true);
                    }

                    boolean found = false;
                    for (Message message : messages) {
                        if (message.getOrder() < messageOrder) {
                            log.debug("removing duplicated message: <{}>", message);
                            messages.remove(message); //to delete duplicates
                        } else if (message.getOrder().equals(messageOrder)) {
                            log.debug("displaying next message: <{}>", message);
                            ui.displayNewMessage(message);
                            messages.remove(message);
                            messageOrder++;
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        for (Message message : messages) { //if there is next message - means we missed something
                            if (message.getOrder() > messageOrder) {
                                log.debug("message with X order wasn't found, but there is a message with " +
                                        "Y > X order. Ask missing message from server");
                                writer.write(Command.MESSAGE_LOST, new Long[]{messageOrder});
                                break;
                            }
                        }

                        messages.notifyAll();
                        messages.wait();
                    }
                }
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
    }
}
