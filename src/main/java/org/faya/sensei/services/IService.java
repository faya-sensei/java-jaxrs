package org.faya.sensei.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IService<T> {
    /**
     * Get an item from repository by primary key.
     *
     * @param id The id of the item.
     * @return The item.
     */
    default Optional<T> get(final int id) {
        return Optional.empty();
    }

    /**
     * Get an item from repository by unique key.
     *
     * @param key The unique key of the item.
     * @return The item.
     */
    default Optional<T> get(String key)  {
        return Optional.empty();
    }

    /**
     * Get the reference items from repository.
     *
     * @param key The key of the foreign item.
     * @param value The search value of the foreign item.
     * @return The list of items.
     */
    default Collection<T> getBy(String key, String value)  {
        return List.of();
    }

    /**
     * Create an item to repository.
     *
     * @param dto The item to create.
     * @return The created item id.
     */
    Optional<T> create(final T dto);

    /**
     * Update an item in repository.
     *
     * @param dto The updated item.
     * @return The updated item.
     */
    Optional<T> update(final int id, final T dto);

    /**
     * Remove an item from repository.
     *
     * @param id The id of remove item.
     * @return The operation result.
     */
    boolean remove(final int id);
}
