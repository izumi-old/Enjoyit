package edu.druid.enjoyit.server.repository;

import edu.druid.enjoyit.base.entity.Person;

import java.util.Optional;

public interface PersonRepository extends SimpleRepository<Person, Long> {
    Optional<Person> findByUsername(String username);
}
