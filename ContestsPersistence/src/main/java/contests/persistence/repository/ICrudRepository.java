package contests.persistence.repository;

import contests.model.Identifiable;

import java.util.Collection;
import java.util.Optional;

public interface ICrudRepository<ID, E extends Identifiable<ID>> {
    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity
     * if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    void add(E entity);

    void delete(E entity);

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     */
    void update(ID id, E entity);

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findById(ID id);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    Collection<E> getAll();
}
