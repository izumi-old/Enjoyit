package edu.kozhinov.enjoyit.server.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

@PropertySource("classpath:server/application.properties")
@ComponentScan("edu.kozhinov.enjoyit.server")
@Import(edu.kozhinov.enjoyit.base.config.Config.class)
@Configuration
public class Config {

    @Bean("executor queue")
    public BlockingQueue<Runnable> queue(
            @Value("${executor.queue.capacity}") Integer queueCapacity,
            @Value("${executor.queue.fair}") Boolean queueFair
    ) {
        return new ArrayBlockingQueue<>(queueCapacity, queueFair);
    }

    @Bean
    public ServerSocket serverSocket(@Value("${ip.port}") Integer port) throws IOException {
        return new ServerSocket(port);
    }

    @Bean
    public Executor executor(
            @Value("${executor.pool.core.size}") Integer corePoolSize,
            @Value("${executor.pool.maximum.size}") Integer maximumPoolSize,
            @Value("${executor.keep.alive.time.seconds}") Long keepAliveTimeSeconds,
            @Qualifier("executor queue") BlockingQueue<Runnable> queue
    ) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeSeconds, TimeUnit.SECONDS, queue);
    }
}
