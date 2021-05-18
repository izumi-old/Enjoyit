package edu.druid.enjoyit.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@PropertySource("classpath:application.properties")
@ComponentScan("edu.druid.enjoyit.server")
@Import(edu.druid.enjoyit.base.config.Config.class)
@Configuration
public class Config {
    private Environment environment;

    @Bean
    public ServerSocket serverSocket() throws IOException {
        return new ServerSocket(environment.getProperty("port", Integer.class));
    }

    @Bean
    public Executor executor() {
        return new ThreadPoolExecutor(32, 64, 1,
                TimeUnit.HOURS, new ArrayBlockingQueue<>(128, true));
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
