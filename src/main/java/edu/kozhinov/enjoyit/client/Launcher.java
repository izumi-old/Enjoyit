package edu.kozhinov.enjoyit.client;

import edu.kozhinov.enjoyit.client.component.Client;
import edu.kozhinov.enjoyit.client.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Launcher {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(Client.class).start();
        log.info("The client application started successfully");
    }
}
