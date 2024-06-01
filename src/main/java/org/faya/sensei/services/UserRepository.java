package org.faya.sensei.services;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.faya.sensei.entities.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IRepository<UserEntity> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Collection<UserEntity> get() {
        return List.of();
    }

    @Override
    public int post(UserEntity item) {
        return -1;
    }

    @Override
    public Optional<UserEntity> put(int id, UserEntity item) {
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> delete(int id) {
        return Optional.empty();
    }
}
