package edu.kozhinov.enjoyit.server.repository;

import edu.kozhinov.enjoyit.core.entity.Person;

import java.util.Optional;

public interface PersonRepository extends SimpleRepository<Person, Long> {
    Optional<Person> findByUsername(String username);
}
