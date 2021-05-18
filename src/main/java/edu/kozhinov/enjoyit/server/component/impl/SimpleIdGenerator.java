package edu.kozhinov.enjoyit.server.component.impl;

import edu.kozhinov.enjoyit.server.component.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SimpleIdGenerator implements IdGenerator {
    private static final Map<Class<?>, AtomicLong> values = new ConcurrentHashMap<>();

    @Override
    public long generate(Class<?> clazz) {
        if (!values.containsKey(clazz)) {
            values.put(clazz, new AtomicLong(2L));
            return 1L;
        }

        return values.get(clazz).getAndIncrement();
    }
}
