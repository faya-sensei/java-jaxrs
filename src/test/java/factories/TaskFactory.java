package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import wrappers.TaskEntityWrapper;

import java.time.LocalDateTime;

public class TaskFactory {


    public static TaskEntityWrapper createTaskEntity(String title, String description, LocalDateTime endDate,
                                                     ProjectEntity projectEntity, StatusEntity statusEntity,
                                                     UserEntity assignerEntity) {
        TaskEntityWrapper wrapper = new TaskEntityWrapper(new TaskEntity());

        wrapper.setTitle(title);
        wrapper.setDescription(description);
        wrapper.setStartDate(LocalDateTime.now());
        wrapper.setEndDate(endDate);
        wrapper.setProject(projectEntity);
        wrapper.setStatus(statusEntity);
        wrapper.setAssigner(assignerEntity);

        return wrapper;
    }
}
