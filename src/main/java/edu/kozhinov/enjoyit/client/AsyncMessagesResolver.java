package edu.kozhinov.enjoyit.client;

import edu.kozhinov.enjoyit.core.entity.Message;
import edu.kozhinov.enjoyit.client.ui.Ui;
import edu.kozhinov.enjoyit.core.async.AsyncComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Slf4j
public class AsyncMessagesResolver extends AsyncComponent {
    private final BlockingQueue<Message> messages;
    private final Ui ui;

    private Long messageOrder;
    private final Lock lock = new ReentrantLock();

    private final Collection<Message> delayed = new LinkedList<>();
    private boolean necessaryCheckDelayed = false;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Message message = messages.take();
                if (messageOrder == null || message.getOrder() <= 2) {
                    log.debug("init order");
                    messageOrder = message.getOrder();
                }
                resolve(message);
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
    }

    public void resetOrder() {
        try {
            log.debug("reset order");
            lock.lock();
            this.messageOrder = null;
            this.delayed.clear();
            necessaryCheckDelayed = false;
        } finally {
            lock.unlock();
        }
    }

    private void resolve(Message message) {
        try {
            lock.lock();
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
        } finally {
            lock.unlock();
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
