package wrappers;

import org.faya.sensei.payloads.TaskDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.time.LocalDateTime;

public record TaskDTOWrapper(TaskDTO taskDTO) {

    private static final VarHandle idHandle;
    private static final VarHandle titleHandle;
    private static final VarHandle descriptionHandle;
    private static final VarHandle startDateHandle;
    private static final VarHandle endDateHandle;
    private static final VarHandle statusHandle;
    private static final VarHandle projectIdHandle;
    private static final VarHandle assignerIdHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(TaskDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(TaskDTO.class, "id", Integer.class);
            titleHandle = lookup.findVarHandle(TaskDTO.class, "title", String.class);
            descriptionHandle = lookup.findVarHandle(TaskDTO.class, "description", String.class);
            startDateHandle = lookup.findVarHandle(TaskDTO.class, "startDate", LocalDateTime.class);
            endDateHandle = lookup.findVarHandle(TaskDTO.class, "endDate", LocalDateTime.class);
            statusHandle = lookup.findVarHandle(TaskDTO.class, "status", String.class);
            projectIdHandle = lookup.findVarHandle(TaskDTO.class, "projectId", Integer.class);
            assignerIdHandle = lookup.findVarHandle(TaskDTO.class, "assignerId", Integer.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(taskDTO);
    }

    public void setId(Integer id) {
        idHandle.set(taskDTO, id);
    }

    public String getTitle() {
        return (String) titleHandle.get(taskDTO);
    }

    public void setTitle(String title) {
        titleHandle.set(taskDTO, title);
    }

    public String getDescription() {
        return (String) descriptionHandle.get(taskDTO);
    }

    public void setDescription(String description) {
        descriptionHandle.set(taskDTO, description);
    }

    public LocalDateTime getStartDate() {
        return (LocalDateTime) startDateHandle.get(taskDTO);
    }

    public void setStartDate(LocalDateTime startDate) {
        startDateHandle.set(taskDTO, startDate);
    }

    public LocalDateTime getEndDate() {
        return (LocalDateTime) endDateHandle.get(taskDTO);
    }

    public void setEndDate(LocalDateTime endDate) {
        endDateHandle.set(taskDTO, endDate);
    }

    public String getStatus() {
        return (String) statusHandle.get(taskDTO);
    }

    public void setStatus(String status) {
        statusHandle.set(taskDTO, status);
    }

    public Integer getProjectId() {
        return (Integer) projectIdHandle.get(taskDTO);
    }

    public void setProjectId(Integer projectId) {
        projectIdHandle.set(taskDTO, projectId);
    }

    public Integer getAssignerId() {
        return (Integer) assignerIdHandle.get(taskDTO);
    }

    public void setAssignerId(Integer assignerId) {
        assignerIdHandle.set(taskDTO, assignerId);
    }
}
