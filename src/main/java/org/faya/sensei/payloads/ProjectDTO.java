package org.faya.sensei.payloads;

import org.faya.sensei.entities.ProjectEntity;

import java.util.List;

public final class ProjectDTO {

    private Integer id;

    private String name;

    private List<TaskDTO> tasks;

    // Converters

    public static ProjectEntity toEntity(final ProjectDTO projectDTO) {
        return new ProjectEntity();
    }

    public static ProjectDTO fromEntity(final ProjectEntity projectEntity) {
        return new ProjectDTO();
    }
}
