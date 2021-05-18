package edu.kozhinov.enjoyit.protocol.mapper;

import edu.kozhinov.enjoyit.protocol.entity.Status;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StatusMapper implements BiMapper<String, Status> {
    @Override
    public Status map1(String s) {
        if (s == null) {
            return null;
        }

        return Arrays.stream(Status.values())
                .filter(status -> status.asString().equals(s))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String map2(Status status) {
        if (status == null) {
            return null;
        }

        return status.asString();
    }
}
