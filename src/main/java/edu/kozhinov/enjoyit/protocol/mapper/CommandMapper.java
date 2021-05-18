package edu.kozhinov.enjoyit.protocol.mapper;

import edu.kozhinov.enjoyit.protocol.entity.Command;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
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
