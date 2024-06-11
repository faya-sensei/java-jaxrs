package org.faya.sensei.payloads;

import org.faya.sensei.entities.StatusEntity;

public final class StatusDTO {

    private Integer id;

    private String name;

    // Converters

    public static StatusEntity toEntity(final StatusDTO statusDTO) {
        return new StatusEntity();
    }

    public static StatusDTO fromEntity(final StatusEntity statusEntity) {
        return new StatusDTO();
    }
}
