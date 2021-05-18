package edu.druid.enjoyit.base.protocol.mapper;

import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.annotation.Mapper;
import edu.druid.enjoyit.base.protocol.entity.Data;

import java.util.Arrays;

@Mapper
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
