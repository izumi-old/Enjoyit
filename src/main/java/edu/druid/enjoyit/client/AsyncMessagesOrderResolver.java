package edu.druid.enjoyit.client;

import edu.druid.enjoyit.base.protocol.AsyncComponent;
import edu.druid.enjoyit.base.protocol.entity.Command;
import edu.druid.enjoyit.base.protocol.io.Writer;
import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.client.ui.Ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.druid.enjoyit.base.protocol.utils.Utils.getCallerSimpleClassName;

public class AsyncMessagesOrderResolver extends AsyncComponent {
    private static final Logger log = LoggerFactory.getLogger(getCallerSimpleClassName());

    public AsyncMessagesOrderResolver(Collection<Message> messages, Ui ui, Writer writer, AtomicBoolean initMarker) {
        super(new Thread(new Resolver(messages, ui, writer, initMarker)));
    }

    private static final class Resolver implements Runnable {
        private final Collection<Message> messages;
        private final Ui ui;
        private final Writer writer;
        private final AtomicBoolean initMarker;

        private long messageOrder;

        public Resolver(Collection<Message> messages, Ui ui, Writer writer, AtomicBoolean initMarker) {
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
}
