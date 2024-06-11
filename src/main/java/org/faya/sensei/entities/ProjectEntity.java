package org.faya.sensei.entities;

import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.List;

@Table(name = "projects")
public class ProjectEntity implements Serializable {

    /**
     * The primary key of the project.
     */
    private Integer id;

    /**
     * The name of the project.
     */
    private String name;

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
