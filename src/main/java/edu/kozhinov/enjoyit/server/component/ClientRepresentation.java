package edu.kozhinov.enjoyit.server.component;

import edu.kozhinov.enjoyit.base.entity.Message;
import edu.kozhinov.enjoyit.base.entity.Person;

public interface ClientRepresentation extends Runnable {
    Person getPerson();
    void sendToClientApplication(Message message);
}
