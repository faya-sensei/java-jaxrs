package wrappers;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record StatusEntityWrapper(StatusEntity entity) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    private static final VarHandle projectHandle;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(StatusEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(StatusEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(StatusEntity.class, "name", String.class);
            projectHandle = lookup.findVarHandle(StatusEntity.class, "project", ProjectEntity.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(entity);
    }

    public void setId(Integer id) {
        idHandle.set(entity, id);
    }

    public String getName() {
        return (String) nameHandle.get(entity);
    }

    public void setName(String name) {
        nameHandle.set(entity, name);
    }

    public ProjectEntityWrapper getProject() {
        return new ProjectEntityWrapper((ProjectEntity) projectHandle.get(entity));
    }

    public void setProject(ProjectEntity project) {
        projectHandle.set(entity, project);
    }
}
