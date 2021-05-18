package edu.kozhinov.enjoyit.server;

import edu.kozhinov.enjoyit.server.component.Server;
import edu.kozhinov.enjoyit.server.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Launcher {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(Server.class).start();
        log.info("The server application started successfully");
    }
}
