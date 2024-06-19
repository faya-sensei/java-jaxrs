package wrappers;

import org.faya.sensei.payloads.ProjectDTO;
import org.faya.sensei.payloads.TaskDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;

@SuppressWarnings("unchecked")
public record ProjectDTOWrapper(ProjectDTO dto) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    private static final VarHandle ownerIdHandle;

    private static final VarHandle tasksHandle;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ProjectDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(ProjectDTO.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(ProjectDTO.class, "name", String.class);
            ownerIdHandle = lookup.findVarHandle(ProjectDTO.class, "ownerId", Integer.class);
            tasksHandle = lookup.findVarHandle(ProjectDTO.class, "tasks", List.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(dto);
    }

    public void setId(Integer id) {
        idHandle.set(dto, id);
    }

    public String getName() {
        return (String) nameHandle.get(dto);
    }

    public void setName(String name) {
        nameHandle.set(dto, name);
    }

    public Integer getOwnerId() {
        return (Integer) ownerIdHandle.get(dto);
    }

    public void setOwnerId(Integer ownerId) {
        ownerIdHandle.set(dto, ownerId);
    }

    public List<TaskDTO> getTasks() {
        return (List<TaskDTO>) tasksHandle.get(dto);
    }

    public void setTasks(List<TaskDTO> tasks) {
        tasksHandle.set(dto, tasks);
    }
}
