package org.faya.sensei.payloads;

import org.faya.sensei.entities.TaskEntity;

import java.time.LocalDateTime;

public final class TaskDTO {

    private Integer id;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    private Integer projectId;

    private Integer assignerId;

    // Converters

    public static TaskEntity toEntity(final TaskDTO taskDTO) {
        return new TaskEntity();
    }

    public static TaskDTO fromEntity(final TaskEntity taskEntity) {
        return new TaskDTO();
    }
}
