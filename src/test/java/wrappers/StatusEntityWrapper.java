package wrappers;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record StatusEntityWrapper(StatusEntity statusEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;
    private static final VarHandle projectHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(StatusEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(StatusEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(StatusEntity.class, "name", String.class);
            projectHandle = lookup.findVarHandle(StatusEntity.class, "project", ProjectEntity.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(statusEntity);
    }

    public void setId(Integer id) {
        idHandle.set(statusEntity, id);
    }

    public String getName() {
        return (String) nameHandle.get(statusEntity);
    }

    public void setName(String name) {
        nameHandle.set(statusEntity, name);
    }

    public ProjectEntityWrapper getProject() {
        return new ProjectEntityWrapper((ProjectEntity) projectHandle.get(statusEntity));
    }

    public void setProject(ProjectEntity project) {
        projectHandle.set(statusEntity, project);
    }
}
