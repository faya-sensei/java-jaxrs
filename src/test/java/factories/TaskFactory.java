package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.repositories.IRepository;
import wrappers.TaskEntityWrapper;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public static IRepository<TaskEntity> createInMemoryTaskRepository() {
        return new IRepository<>() {

            @Override
            public int post(TaskEntity item) {
                return 0;
            }

            @Override
            public Optional<TaskEntity> put(int id, TaskEntity item) {
                return Optional.empty();
            }

            @Override
            public Optional<TaskEntity> delete(int id) {
                return Optional.empty();
            }
        };
    }
}
