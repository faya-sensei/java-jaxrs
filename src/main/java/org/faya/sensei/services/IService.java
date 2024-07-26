package org.faya.sensei.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IService<T> {
    /**
     * Retrieves an item from the repository by primary key.
     *
     * @param id The primary key of the item.
     * @return The nullable item.
     */
    default Optional<T> get(final int id) {
        return Optional.empty();
    }

    /**
     * Retrieves an item from repository by unique key.
     *
     * @param key The unique key of the item.
     * @return The nullable item.
     */
    default Optional<T> get(final String key) {
        return Optional.empty();
    }

    /**
     * Retrieves a collection of items from repository based on a foreign key.
     *
     * @param key The key of the foreign item.
     * @param value The search value of the foreign item.
     * @return A collection of items.
     */
    default Collection<T> getBy(final String key, final String value) {
        return List.of();
    }

    /**
     * Create a new item in the repository.
     *
     * @param dto The data transfer object.
     * @return The nullable created item.
     */
    Optional<T> create(final T dto);

    /**
     * Update an existing item in existing repository.
     *
     * @param id The primary key of the item to be updated.
     * @param dto The data transfer object.
     * @return The nullable updated item.
     */
    Optional<T> update(final int id, final T dto);

    /**
     * Remove an item from the repository based on id.
     *
     * @param id The primary key of the item to be removed.
     * @return The operation result.
     */
    boolean remove(final int id);
}
