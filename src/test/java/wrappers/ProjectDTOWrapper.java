package wrappers;

import org.faya.sensei.payloads.ProjectDTO;
import org.faya.sensei.payloads.TaskDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;

@SuppressWarnings("unchecked")
public record ProjectDTOWrapper(ProjectDTO projectDTO) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;
    private static final VarHandle tasksHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ProjectDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(ProjectDTO.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(ProjectDTO.class, "name", String.class);
            tasksHandle = lookup.findVarHandle(ProjectDTO.class, "tasks", List.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(projectDTO);
    }

    public void setId(Integer id) {
        idHandle.set(projectDTO, id);
    }

    public String getName() {
        return (String) nameHandle.get(projectDTO);
    }

    public void setName(String name) {
        nameHandle.set(projectDTO, name);
    }

    public List<TaskDTO> getTasks() {
        return (List<TaskDTO>) tasksHandle.get(projectDTO);
    }

    public void setTasks(List<TaskDTO> tasks) {
        tasksHandle.set(projectDTO, tasks);
    }
}
