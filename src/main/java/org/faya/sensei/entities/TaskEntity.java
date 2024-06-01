package org.faya.sensei.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskEntity implements Serializable {

    /**
     * The primary key of the task.
     */
    private Integer id;

    /**
     * The summary title of the task.
     */
    private String title;

    /**
     * The detail description of the task.
     */
    private String description;

    /**
     * The start date time of the task.
     */
    private LocalDateTime startDate;

    /**
     * The due date time of the task.
     */
    private LocalDateTime endDate;

    /**
     * The many-to-one relationship with {@link BoardEntity}.
     */
    private BoardEntity board;

    /**
     * The many-to-one relationship with {@link StatusEntity}.
     */
    private StatusEntity status;

    /**
     * The many-to-one relationship with {@link UserEntity}.
     */
    private UserEntity assigner;
}
