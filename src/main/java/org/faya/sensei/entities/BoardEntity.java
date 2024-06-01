package org.faya.sensei.entities;

import java.io.Serializable;
import java.util.List;

public class BoardEntity implements Serializable {

    /**
     * The primary key of the board.
     */
    private Integer id;

    /**
     * The many-to-many relationship with {@link UserEntity}. (hidden in database)
     */
    private List<UserEntity> users;

    /**
     * The one-to-many relationship with {@link StatusEntity}. (hidden in database)
     */
    private List<StatusEntity> statuses;

    /**
     * The one-to-many relationship with {@link TaskEntity}. (hidden in database)
     */
    private List<TaskEntity> tasks;
}
