package edu.kozhinov.enjoyit.server.repository.impl.stub;

import edu.kozhinov.enjoyit.core.entity.Person;
import edu.kozhinov.enjoyit.server.component.IdGenerator;
import edu.kozhinov.enjoyit.server.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Repository
public class PersonRepositoryStub implements PersonRepository {
    private static final Map<Long, Person> data = new ConcurrentHashMap<>();
    private static final AtomicBoolean initMarker = new AtomicBoolean(false);

    private final IdGenerator generator;

    @PostConstruct
    private void init() {
        if (!initMarker.get()) {
            Person p1 = new Person("Dima", "a");
            Person p2 = new Person("Alex", "b");
            Person p3 = new Person("Max", "c");
            Person p4 = new Person("Aiden", "d");
            save(p1);
            save(p2);
            save(p3);
            save(p4);
            initMarker.set(true);
        }
    }

    @Override
    public <S extends Person> S save(S s) {
        if (s.getId() == null || data.containsKey(s.getId())) {
            s.setId(generator.generate(Person.class));
        }
        data.put(s.getId(), s);
        return (S) data.get(s.getId());
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        for (Map.Entry<Long, Person> entry : data.entrySet()) {
            Person person = entry.getValue();
            if (person.getUsername().equals(username)) {
                return Optional.of(person);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Person> findById(Long aLong) {
        Person person = data.get(aLong);
        return person == null ? Optional.empty() : Optional.of(person);
    }

    @Override
    public Iterable<Person> findAll() {
        return data.values();
    }

    @Override
    public boolean existsById(Long aLong) {
        return data.get(aLong) != null;
    }

    @Override
    public <T extends Person> Optional<T> update(Long aLong, T t) {
        for (Map.Entry<Long, Person> entry : data.entrySet()) {
            Person person = entry.getValue();
            if (person.getClass() == t.getClass() && person.equals(t)) {
                return Optional.of((T) data.put(aLong, person));
            }
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long aLong) {
        data.remove(aLong);
    }

    @Override
    public void delete(Person person) {
        long id = -1L;
        for (Map.Entry<Long, Person> entry : data.entrySet()) {
            if (entry.getValue().equals(person)) {
                id = entry.getKey();
                break;
            }
        }
        deleteById(id);
    }
}
