package edu.kozhinov.enjoyit.server.repository;

import edu.kozhinov.enjoyit.base.entity.Person;

import java.util.Optional;

public interface PersonRepository extends SimpleRepository<Person, Long> {
    Optional<Person> findByUsername(String username);
}
