package org.faya.sensei.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    /**
     * Fetch all items from storage.
     *
     * @return The collection of items.
     */
    default Collection<T> get() {
        return List.of();
    }

    /**
     * Fetch an item from storage by primary key.
     *
     * @param id The primary key of the item.
     * @return The item.
     */
    default Optional<T> get(final int id) {
        return Optional.empty();
    }

    /**
     * Fetch an item from storage by unique key.
     *
     * @param key The unique key of the item.
     * @return The item.
     */
    default Optional<T> get(final String key)  {
        return Optional.empty();
    }

    /**
     * Fetch an item from storage based on foreign key name.
     *
     * @param key The name of the foreign key.
     * @param value The query value of the foreign key.
     * @return The collection of items.
     */
    default Collection<T> getBy(final String key, final String value)  {
        return List.of();
    }

    /**
     * Save an item to storage.
     *
     * @param item The item to create.
     * @return The saved item id.
     */
    int post(T item);

    /**
     * Update an item based on id.
     *
     * @param id The id of the item.
     * @param item The updated item.
     * @return The updated item.
     */
    Optional<T> put(final int id, T item);

    /**
     * Remove an item from storage according to id.
     *
     * @param id The id of the item.
     * @return The removed item.
     */
    Optional<T> delete(final int id);
}
