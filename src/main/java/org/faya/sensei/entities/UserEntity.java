package org.faya.sensei.entities;

import java.io.Serializable;
import java.util.List;

public class UserEntity implements Serializable {

    /**
     * The primary key of the user.
     */
    private Integer id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The many-to-many relationship with {@link BoardEntity}. (hidden in database)
     */
    private List<BoardEntity> boards;

    /**
     * The one-to-many relationship with {@link BoardEntity}. (hidden in database)
     */
    private List<TaskEntity> assignedTasks;
}
