package edu.kozhinov.enjoyit.server.repository;

import java.util.Optional;

public interface SimpleRepository<Entity, ID> {
    <T extends Entity> T save(T t);
    Optional<Entity> findById(ID id);
    Iterable<Entity> findAll();
    boolean existsById(ID id);
    <T extends Entity> Optional<T> update(ID id, T t);
    void deleteById(ID id);
    void delete(Entity entity);
}
