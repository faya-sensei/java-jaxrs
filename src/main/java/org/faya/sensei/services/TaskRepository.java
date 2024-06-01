package org.faya.sensei.services;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.faya.sensei.entities.TaskEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TaskRepository implements IRepository<TaskEntity> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Collection<TaskEntity> get() {
        return List.of();
    }

    @Override
    public int post(TaskEntity item) {
        return -1;
    }

    @Override
    public Optional<TaskEntity> put(int id, TaskEntity item) {
        return Optional.empty();
    }

    @Override
    public Optional<TaskEntity> delete(int id) {
        return Optional.empty();
    }
}
