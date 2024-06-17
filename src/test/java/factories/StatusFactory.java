package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import wrappers.StatusEntityWrapper;

public class StatusFactory {

    public static StatusEntityWrapper createStatusEntity(final String name, final ProjectEntity project) {
        StatusEntityWrapper wrapper = new StatusEntityWrapper(new StatusEntity());

        wrapper.setName(name);
        wrapper.setProject(project);

        return wrapper;
    }
}
