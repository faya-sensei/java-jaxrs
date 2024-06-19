package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import wrappers.StatusEntityWrapper;

public class StatusFactory {

    public static StatusEntityBuilder createStatusEntity() {
        return new StatusEntityBuilder();
    }

    public static StatusEntityBuilder createStatusEntity(final StatusEntityWrapper wrapper) {
        return new StatusEntityBuilder(wrapper);
    }

    public static StatusEntityBuilder createStatusEntity(final String name, final ProjectEntity project) {
        return createStatusEntity().setName(name).setProject(project);
    }

    public static StatusEntityBuilder createStatusEntity(final int id, final String name, final ProjectEntity project) {
        return createStatusEntity().setId(id).setName(name).setProject(project);
    }

    public static class StatusEntityBuilder {

        private final StatusEntityWrapper wrapper;

        public StatusEntityBuilder() {
            this.wrapper = new StatusEntityWrapper(new StatusEntity());
        }

        public StatusEntityBuilder(final StatusEntityWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public StatusEntityBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public StatusEntityBuilder setName(final String name) {
            wrapper.setName(name);
            return this;
        }

        public StatusEntityBuilder setProject(final ProjectEntity project) {
            wrapper.setProject(project);
            return this;
        }

        public StatusEntityWrapper build() {
            return wrapper;
        }

        public StatusEntity toEntity() {
            return wrapper.entity();
        }
    }
}
