package org.faya.sensei.payloads;

import java.time.LocalDateTime;

public final class TaskDTO {

    private Integer id;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    private Integer boardId;

    private Integer assignerId;
}
