package edu.druid.enjoyit.base.protocol.mapper;

import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.annotation.Mapper;
import edu.druid.enjoyit.base.protocol.entity.Status;

import java.util.Arrays;

@Mapper
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
