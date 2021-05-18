package edu.kozhinov.enjoyit.protocol.mapper;

import edu.kozhinov.enjoyit.protocol.entity.Data;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataTypeMapper implements BiMapper<String, Data.Type> {
    @Override
    public Data.Type map1(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        return Arrays.stream(Data.Type.values())
                .filter(type -> type.toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String map2(Data.Type type) {
        if (type == null) {
            return null;
        }

        return type.toString();
    }
}
