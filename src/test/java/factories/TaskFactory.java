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

    public static TaskDTOBuilder createTaskDTO() {
        return new TaskDTOBuilder(new TaskDTOWrapper(new TaskDTO()));
    }

    public static TaskDTOBuilder createTaskDTO(final TaskDTOWrapper wrapper) {
        return new TaskDTOBuilder(wrapper);
    }

    public static TaskDTOBuilder createTaskDTO(
            final String title,
            final String description,
            final LocalDateTime endDate,
            final String status,
            final Integer projectId,
            final Integer assignerId
    ) {
        return createTaskDTO()
                .setTitle(title)
                .setDescription(description)
                .setStartDate(LocalDateTime.now())
                .setEndDate(endDate)
                .setStatus(status)
                .setProjectId(projectId)
                .setAssignerId(assignerId);
    }

    public static TaskEntityBuilder createTaskEntity() {
        return new TaskEntityBuilder(new TaskEntityWrapper(new TaskEntity()));
    }

    public static TaskEntityBuilder createTaskEntity(final TaskEntityWrapper wrapper) {
        return new TaskEntityBuilder(wrapper);
    }

    public static TaskEntityBuilder createTaskEntity(
            final String title,
            final String description,
            final LocalDateTime endDate,
            final StatusEntity status,
            final ProjectEntity project,
            final UserEntity assigner
    ) {
        return createTaskEntity()
                .setTitle(title)
                .setDescription(description)
                .setStartDate(LocalDateTime.now())
                .setEndDate(endDate)
                .setStatus(status)
                .setProject(project)
                .setAssigner(assigner);
    }

    public static TaskEntityBuilder createTaskEntity(
            final int id,
            final String title,
            final String description,
            final LocalDateTime endDate,
            final StatusEntity status,
            final ProjectEntity project,
            final UserEntity assigner
    ) {
        return createTaskEntity()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setStartDate(LocalDateTime.now())
                .setEndDate(endDate)
                .setStatus(status)
                .setProject(project)
                .setAssigner(assigner);
    }

    public static class TaskDTOBuilder {

        private final TaskDTOWrapper wrapper;

        public TaskDTOBuilder() {
            this.wrapper = new TaskDTOWrapper(new TaskDTO());
        }

        public TaskDTOBuilder(final TaskDTOWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public TaskDTOBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public TaskDTOBuilder setTitle(final String title) {
            wrapper.setTitle(title);
            return this;
        }

        public TaskDTOBuilder setDescription(final String description) {
            wrapper.setDescription(description);
            return this;
        }

        public TaskDTOBuilder setStartDate(final LocalDateTime startDate) {
            wrapper.setStartDate(startDate);
            return this;
        }

        public TaskDTOBuilder setEndDate(final LocalDateTime endDate) {
            wrapper.setEndDate(endDate);
            return this;
        }

        public TaskDTOBuilder setStatus(final String status) {
            wrapper.setStatus(status);
            return this;
        }

        public TaskDTOBuilder setProjectId(final int projectId) {
            wrapper.setProjectId(projectId);
            return this;
        }

        public TaskDTOBuilder setAssignerId(final int assignerId) {
            wrapper.setAssignerId(assignerId);
            return this;
        }

        public TaskDTOWrapper build() {
            return wrapper;
        }

        public TaskDTO toDTO() {
            return wrapper.dto();
        }
    }

    public static class TaskEntityBuilder {

        private final TaskEntityWrapper wrapper;

        public TaskEntityBuilder() {
            this.wrapper = new TaskEntityWrapper(new TaskEntity());
        }

        public TaskEntityBuilder(final TaskEntityWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public TaskEntityBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public TaskEntityBuilder setTitle(final String title) {
            wrapper.setTitle(title);
            return this;
        }

        public TaskEntityBuilder setDescription(final String description) {
            wrapper.setDescription(description);
            return this;
        }

        public TaskEntityBuilder setStartDate(final LocalDateTime startDate) {
            wrapper.setStartDate(startDate);
            return this;
        }

        public TaskEntityBuilder setEndDate(final LocalDateTime endDate) {
            wrapper.setEndDate(endDate);
            return this;
        }

        public TaskEntityBuilder setStatus(final StatusEntity status) {
            wrapper.setStatus(status);
            return this;
        }

        public TaskEntityBuilder setProject(final ProjectEntity project) {
            wrapper.setProject(project);
            return this;
        }

        public TaskEntityBuilder setAssigner(final UserEntity assigner) {
            wrapper.setAssigner(assigner);
            return this;
        }

        public TaskEntityWrapper build() {
            return wrapper;
        }

        public TaskEntity toEntity() {
            return wrapper.entity();
        }
    }
}
