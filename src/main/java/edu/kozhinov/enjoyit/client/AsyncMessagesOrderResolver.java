package edu.kozhinov.enjoyit.client;

import edu.kozhinov.enjoyit.base.entity.Message;
import edu.kozhinov.enjoyit.client.ui.Ui;
import edu.kozhinov.enjoyit.protocol.AsyncComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class AsyncMessagesOrderResolver extends AsyncComponent {
    private final BlockingQueue<Message> messages;
    private final Ui ui;

    private Long messageOrder;

    private final Collection<Message> delayed = new LinkedList<>();
    private boolean necessaryCheckDelayed = false;

    public AsyncMessagesOrderResolver(BlockingQueue<Message> messages, Ui ui) {
        this.messages = messages;
        this.ui = ui;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Message message = messages.take();
                if (messageOrder == null) {
                    messageOrder = message.getOrder();
                }
                resolve(message);
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
    }

    private void resolve(Message message) {
        if (message.getOrder() < messageOrder) {
            log.debug("removing duplicated message: <{}>", message);
            messages.remove(message); //to delete duplicates
        } else if (message.getOrder() > messageOrder) {
            delayed.add(message);
            necessaryCheckDelayed = true;
        } else {
            dealWithFitMessage(message);

            while (necessaryCheckDelayed) {
                checkDelayed();
            }
        }
    }

    private void dealWithFitMessage(Message message) {
        log.debug("displaying next message: <{}>", message);
        ui.displayNewMessage(message);
        messageOrder++;
    }

    private void checkDelayed() {
        for (Message delayedMessage : delayed) {
            if (delayedMessage.getOrder().equals(messageOrder)) {
                delayed.remove(delayedMessage);
                dealWithFitMessage(delayedMessage);
                return;
            }
        }
        necessaryCheckDelayed = false;
    }
}
