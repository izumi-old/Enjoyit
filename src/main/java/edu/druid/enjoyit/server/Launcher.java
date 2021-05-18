package edu.druid.enjoyit.server;

import edu.druid.enjoyit.server.component.Server;
import edu.druid.enjoyit.server.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static edu.druid.enjoyit.base.protocol.utils.Constants.GENERAL_LOGGER_NAME;

public class Launcher {
    private static final Logger log = LoggerFactory.getLogger(GENERAL_LOGGER_NAME);

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(Server.class).start();
        log.info("The server application started successfully");
    }
}
