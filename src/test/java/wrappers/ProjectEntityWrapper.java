package wrappers;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.UserEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;

@SuppressWarnings("unchecked")
public record ProjectEntityWrapper(ProjectEntity projectEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;
    private static final VarHandle usersHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ProjectEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(ProjectEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(ProjectEntity.class, "name", String.class);
            usersHandle = lookup.findVarHandle(ProjectEntity.class, "users", List.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(projectEntity);
    }

    public void setId(Integer id) {
        idHandle.set(projectEntity, id);
    }

    public String getName() {
        return (String) nameHandle.get(projectEntity);
    }

    public void setName(String name) {
        nameHandle.set(projectEntity, name);
    }

    public List<UserEntity> getUsers() {
        return (List<UserEntity>) usersHandle.get(projectEntity);
    }

    public void setUsers(List<UserEntity> users) {
        usersHandle.set(projectEntity, users);
    }
}
