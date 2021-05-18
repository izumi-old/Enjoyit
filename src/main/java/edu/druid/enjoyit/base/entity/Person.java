package edu.druid.enjoyit.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.druid.enjoyit.server.component.IdGenerator;
import edu.druid.enjoyit.server.component.impl.SimpleIdGenerator;
import lombok.Data;
import lombok.ToString;

import static edu.druid.enjoyit.server.utils.Constants.UNNAMED_PERSON;

@JsonIgnoreProperties({"currentRoom"})
@Data
public class Person {
    private static final IdGenerator generator = new SimpleIdGenerator();
    private static final String FORMAT = "%s #%d";

    private Long id; //unique, not null

    @ToString.Exclude
    private Room currentRoom; //not null

    private String username; //not blank
    private String password; //encoded, nullable
    private boolean anonymous;

    public Person() {
        this.username = String.format(FORMAT, UNNAMED_PERSON, generator.generate(AnonymousStub.class));
        this.anonymous = true;
    }

    public Person(String username, String password) {
        this.username = username;
        this.password = password;
        this.anonymous = false;
    }

    public void setPassword(String password) {
        this.password = password;
        this.anonymous = false;
    }

    public boolean fit(String password) {
        return this.password == null || this.password.equals(password);
    }

    private static final class AnonymousStub {}
}
