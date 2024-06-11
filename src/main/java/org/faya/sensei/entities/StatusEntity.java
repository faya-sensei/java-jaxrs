package org.faya.sensei.entities;

import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.List;

@Table(name = "statuses")
public class StatusEntity implements Serializable {

    /**
     * The primary key of the status.
     */
    private Integer id;

    /**
     * The name of the status.
     */
    private String name;

    /**
     * The many-to-one relationship with {@link ProjectEntity}.
     */
    private ProjectEntity project;

    /**
     * The one-to-many relationship with {@link TaskEntity}. (hidden in database)
     */
    private List<TaskEntity> tasks;
}
