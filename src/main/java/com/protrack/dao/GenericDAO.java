package com.protrack.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) interface demonstrating Generics and Abstraction.
 *
 * @param <T> Entity type
 * @param <ID> Primary key identifier type
 */
public interface GenericDAO<T, ID> {

    /**
     * Retrieves an entity by its unique ID.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities in the table.
     */
    List<T> findAll();

    /**
     * Inserts a new entity record into the database.
     */
    T save(T entity);

    /**
     * Updates an existing entity record in the database.
     */
    boolean update(T entity);

    /**
     * Deletes an entity record by its unique ID.
     */
    boolean deleteById(ID id);
}
