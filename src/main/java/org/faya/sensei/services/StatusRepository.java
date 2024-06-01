package org.faya.sensei.services;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.faya.sensei.entities.StatusEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StatusRepository implements IRepository<StatusEntity> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Collection<StatusEntity> get() {
        return List.of();
    }

    @Override
    public int post(StatusEntity item) {
        return -1;
    }

    @Override
    public Optional<StatusEntity> put(int id, StatusEntity item) {
        return Optional.empty();
    }

    @Override
    public Optional<StatusEntity> delete(int id) {
        return Optional.empty();
    }
}
