package factories;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.ProjectDTO;
import wrappers.ProjectDTOWrapper;
import wrappers.ProjectEntityWrapper;

import java.util.List;

public class ProjectFactory {

    public static ProjectDTOWrapper createProjectDTO(String name) {
        ProjectDTOWrapper wrapper = new ProjectDTOWrapper(new ProjectDTO());

        wrapper.setName(name);

        return wrapper;
    }

    public static ProjectEntityWrapper createProjectEntity(String name, List<UserEntity> users) {
        ProjectEntityWrapper wrapper = new ProjectEntityWrapper(new ProjectEntity());

        wrapper.setName(name);
        wrapper.setUsers(users);

        return wrapper;
    }
}
