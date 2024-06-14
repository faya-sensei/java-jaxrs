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

    public static TaskDTOWrapper createTaskDTO(String title, String description, LocalDateTime endDate,
                                               String status, Integer projectId, Integer assignerId) {
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

    public static TaskEntityWrapper createTaskEntity(String title, String description, LocalDateTime endDate,
                                                     StatusEntity statusEntity, ProjectEntity projectEntity,
                                                     UserEntity assignerEntity) {
        TaskEntityWrapper wrapper = new TaskEntityWrapper(new TaskEntity());

        wrapper.setTitle(title);
        wrapper.setDescription(description);
        wrapper.setStartDate(LocalDateTime.now());
        wrapper.setEndDate(endDate);
        wrapper.setStatus(statusEntity);
        wrapper.setProject(projectEntity);
        wrapper.setAssigner(assignerEntity);

        return wrapper;
    }
}
