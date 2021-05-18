package edu.kozhinov.enjoyit.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.Socket;

@PropertySource("classpath:client/application.properties")
@ComponentScan("edu.kozhinov.enjoyit.client")
@Import(edu.kozhinov.enjoyit.base.config.Config.class)
@Configuration
public class Config {

    @Bean
    public Socket socket(
            @Value("${ip.address}") String address,
            @Value("${ip.port}") Integer port
    ) throws IOException {
        return new Socket(address, port);
    }
}
