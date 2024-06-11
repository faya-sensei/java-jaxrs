package org.faya.sensei.entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.List;

@Table(name = "users")
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
     * The password of the user.
     */
    private String password;

    /**
     * The role of the user.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * The many-to-many relationship with {@link ProjectEntity}. (hidden in database)
     */
    private List<ProjectEntity> projects;

    /**
     * The one-to-many relationship with {@link ProjectEntity}. (hidden in database)
     */
    private List<TaskEntity> assignedTasks;
}
