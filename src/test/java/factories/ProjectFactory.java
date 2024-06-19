package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.ProjectDTO;
import org.faya.sensei.payloads.TaskDTO;
import wrappers.ProjectDTOWrapper;
import wrappers.ProjectEntityWrapper;

import java.util.List;

public final class ProjectFactory {

    public static ProjectDTOBuilder createProjectDTO() {
        return new ProjectDTOBuilder(new ProjectDTOWrapper(new ProjectDTO()));
    }

    public static ProjectDTOBuilder createProjectDTO(final ProjectDTOWrapper wrapper) {
        return new ProjectDTOBuilder(wrapper);
    }

    public static ProjectDTOBuilder createProjectDTO(final String name) {
        return createProjectDTO().setName(name);
    }

    public static ProjectDTOBuilder createProjectDTO(final String name, final List<TaskDTO> tasks) {
        return createProjectDTO().setName(name).setTasks(tasks);
    }

    public static ProjectEntityBuilder createProjectEntity() {
        return new ProjectEntityBuilder(new ProjectEntityWrapper(new ProjectEntity()));
    }

    public static ProjectEntityBuilder createProjectEntity(final ProjectEntityWrapper wrapper) {
        return new ProjectEntityBuilder(wrapper);
    }

    public static ProjectEntityBuilder createProjectEntity(final String name, final List<UserEntity> users) {
        return createProjectEntity().setName(name).setUsers(users);
    }

    public static ProjectEntityBuilder createProjectEntity(final int id, final String name, final List<UserEntity> users) {
        return createProjectEntity().setId(id).setName(name).setUsers(users);
    }

    public static class ProjectDTOBuilder {

        private final ProjectDTOWrapper wrapper;

        public ProjectDTOBuilder() {
            this.wrapper = new ProjectDTOWrapper(new ProjectDTO());
        }

        public ProjectDTOBuilder(final ProjectDTOWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public ProjectDTOBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public ProjectDTOBuilder setName(final String name) {
            wrapper.setName(name);
            return this;
        }

        public ProjectDTOBuilder setTasks(final List<TaskDTO> tasks) {
            wrapper.setTasks(tasks);
            return this;
        }

        public ProjectDTOWrapper build() {
            return wrapper;
        }

        public ProjectDTO toDTO() {
            return wrapper.dto();
        }
    }

    public static class ProjectEntityBuilder {

        private final ProjectEntityWrapper wrapper;

        public ProjectEntityBuilder() {
            this.wrapper = new ProjectEntityWrapper(new ProjectEntity());
        }

        public ProjectEntityBuilder(final ProjectEntityWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public ProjectEntityBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public ProjectEntityBuilder setName(final String name) {
            wrapper.setName(name);
            return this;
        }

        public ProjectEntityBuilder setUsers(final List<UserEntity> users) {
            wrapper.setUsers(users);
            return this;
        }

        public ProjectEntityWrapper build() {
            return wrapper;
        }

        public ProjectEntity toEntity() {
            return wrapper.entity();
        }
    }
}
