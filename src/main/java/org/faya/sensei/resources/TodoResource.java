package org.faya.sensei.resources;

import jakarta.inject.Inject;
import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.services.IRepository;

public class TodoResource {

    @Inject
    private IRepository<UserEntity> userRepository;

    @Inject
    private IRepository<BoardEntity> boardRepository;

    @Inject
    private IRepository<StatusEntity> statusRepository;

    @Inject
    private IRepository<TaskEntity> taskRepository;
}
