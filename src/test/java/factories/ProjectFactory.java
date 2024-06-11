package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.UserEntity;
import wrappers.ProjectEntityWrapper;

import java.util.List;

public class ProjectFactory {

    public static ProjectEntityWrapper createProjectEntity(List<UserEntity> users) {
        ProjectEntityWrapper wrapper = new ProjectEntityWrapper(new ProjectEntity());

        wrapper.setUsers(users);

        return wrapper;
    }
}
