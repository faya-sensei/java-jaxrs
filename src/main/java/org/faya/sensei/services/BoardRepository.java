package org.faya.sensei.services;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.faya.sensei.entities.BoardEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BoardRepository implements IRepository<BoardEntity> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Collection<BoardEntity> get() {
        return List.of();
    }

    @Override
    public int post(BoardEntity boardEntity) {
        return -1;
    }

    @Override
    public Optional<BoardEntity> put(int id, BoardEntity boardEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<BoardEntity> delete(int id) {
        return Optional.empty();
    }
}
