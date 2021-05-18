package edu.kozhinov.enjoyit.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"room"})
@Data
public class Message {
    private Long id; //unique, not null
    private Person sender; //not null
    private String body; //not blank
    private LocalDateTime creationTimestamp; //not null, before then now
    private Long order; //not null //order in room, prevent data losing when server uses asynchronous sending

    private Room room;

    public Message() {
    }

    public Message(String body, LocalDateTime creationTimestamp) {
        this(null, null, body, creationTimestamp);
    }

    public Message(Person sender, String body, LocalDateTime creationTimestamp) {
        this(null, sender, body, creationTimestamp);
    }

    public Message(Long id, Person sender, String body, LocalDateTime creationTimestamp) {
        this.id = id;
        this.sender = sender;
        this.body = body;
        this.creationTimestamp = creationTimestamp;
    }
}
