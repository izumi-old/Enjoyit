package edu.druid.enjoyit.base.protocol.mapper;

import edu.druid.enjoyit.base.mapper.BiMapper;
import edu.druid.enjoyit.base.annotation.Mapper;
import edu.druid.enjoyit.base.protocol.entity.Command;

import java.util.Arrays;

@Mapper
public class CommandMapper implements BiMapper<String, Command> {
    @Override
    public Command map1(String s) {
        if (s == null) {
            return null;
        }

        return Arrays.stream(Command.values())
                .filter(command -> command.asString().equals(s))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String map2(Command command) {
        if (command == null) {
            return null;
        }

        return command.asString();
    }
}
