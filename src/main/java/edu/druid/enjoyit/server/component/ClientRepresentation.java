package edu.druid.enjoyit.server.component;

import edu.druid.enjoyit.base.entity.Message;
import edu.druid.enjoyit.base.entity.Person;

public interface ClientRepresentation extends Runnable {
    Person getPerson();
    void sendToClientApplication(Message message);
}
