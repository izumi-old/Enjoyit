package edu.druid.enjoyit.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.Socket;

@PropertySource("classpath:application.properties")
@ComponentScan("edu.druid.enjoyit.client")
@Import(edu.druid.enjoyit.base.config.Config.class)
@Configuration
public class Config {
    private Environment environment;

    @Bean
    public Socket socket() throws IOException {
        return new Socket(environment.getProperty("host"), environment.getProperty("port", Integer.class));
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
