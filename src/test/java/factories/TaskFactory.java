package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.TaskDTO;
import wrappers.TaskDTOWrapper;
import wrappers.TaskEntityWrapper;

import java.time.LocalDateTime;

public class TaskFactory {

    public static TaskDTOWrapper createTaskDTO(
            final String title,
            final String description,
            final LocalDateTime endDate,
            final String status,
            final Integer projectId,
            final Integer assignerId
    ) {
        TaskDTOWrapper wrapper = new TaskDTOWrapper(new TaskDTO());

        wrapper.setTitle(title);
        wrapper.setDescription(description);
        wrapper.setStartDate(LocalDateTime.now());
        wrapper.setEndDate(endDate);
        wrapper.setStatus(status);
        wrapper.setProjectId(projectId);
        wrapper.setAssignerId(assignerId);

        return wrapper;
    }

    public static TaskEntityWrapper createTaskEntity(
            final String title,
            final String description,
            final LocalDateTime endDate,
            final StatusEntity status,
            final ProjectEntity project,
            final UserEntity assigner
    ) {
        TaskEntityWrapper wrapper = new TaskEntityWrapper(new TaskEntity());

        wrapper.setTitle(title);
        wrapper.setDescription(description);
        wrapper.setStartDate(LocalDateTime.now());
        wrapper.setEndDate(endDate);
        wrapper.setStatus(status);
        wrapper.setProject(project);
        wrapper.setAssigner(assigner);

        return wrapper;
    }
}
