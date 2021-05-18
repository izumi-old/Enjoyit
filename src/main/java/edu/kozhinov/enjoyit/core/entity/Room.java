package edu.kozhinov.enjoyit.core.entity;

import edu.kozhinov.enjoyit.server.component.IdGenerator;
import edu.kozhinov.enjoyit.server.component.impl.SimpleIdGenerator;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedList;

import static edu.kozhinov.enjoyit.server.Constants.UNNAMED_ROOM;

@Data
public class Room {
    private static final IdGenerator generator = new SimpleIdGenerator();
    private static final String FORMAT = "%s #%d";
    private Long id; //unique, not null
    private String name; //unique, not blank

    private String password; //encoded, nullable
    private boolean secured;

    @ToString.Exclude
    private Collection<Person> persons = new LinkedList<>();

    public Room() {
        this.name = String.format(FORMAT, UNNAMED_ROOM, generator.generate(AnonymousStub.class));
        this.password = "";
        this.secured = false;
    }

    public Room(String name, String password) {
        this.name = name;
        this.password = password;
        this.secured = !password.equals("");
    }

    public void setPassword(String password) {
        this.password = password;
        this.secured = !password.equals("");
    }

    public boolean fit(String password) {
        return this.password.equals("") || this.password.equals(password);
    }

    private static final class AnonymousStub {}
}
