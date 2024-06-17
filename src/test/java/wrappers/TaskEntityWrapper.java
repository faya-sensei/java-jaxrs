package wrappers;

import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.time.LocalDateTime;

public record TaskEntityWrapper(TaskEntity entity) {

    private static final VarHandle idHandle;

    private static final VarHandle titleHandle;

    private static final VarHandle descriptionHandle;

    private static final VarHandle startDateHandle;

    private static final VarHandle endDateHandle;

    private static final VarHandle projectHandle;

    private static final VarHandle statusHandle;

    private static final VarHandle assignerHandle;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(TaskEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(TaskEntity.class, "id", Integer.class);
            titleHandle = lookup.findVarHandle(TaskEntity.class, "title", String.class);
            descriptionHandle = lookup.findVarHandle(TaskEntity.class, "description", String.class);
            startDateHandle = lookup.findVarHandle(TaskEntity.class, "startDate", LocalDateTime.class);
            endDateHandle = lookup.findVarHandle(TaskEntity.class, "endDate", LocalDateTime.class);
            projectHandle = lookup.findVarHandle(TaskEntity.class, "project", ProjectEntity.class);
            statusHandle = lookup.findVarHandle(TaskEntity.class, "status", StatusEntity.class);
            assignerHandle = lookup.findVarHandle(TaskEntity.class, "assigner", UserEntity.class);
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

    public String getTitle() {
        return (String) titleHandle.get(entity);
    }

    public void setTitle(String title) {
        titleHandle.set(entity, title);
    }

    public String getDescription() {
        return (String) descriptionHandle.get(entity);
    }

    public void setDescription(String description) {
        descriptionHandle.set(entity, description);
    }

    public LocalDateTime getStartDate() {
        return (LocalDateTime) startDateHandle.get(entity);
    }

    public void setStartDate(LocalDateTime startDate) {
        startDateHandle.set(entity, startDate);
    }

    public LocalDateTime getEndDate() {
        return (LocalDateTime) endDateHandle.get(entity);
    }

    public void setEndDate(LocalDateTime endDate) {
        endDateHandle.set(entity, endDate);
    }

    public ProjectEntityWrapper getProject() {
        return new ProjectEntityWrapper((ProjectEntity) projectHandle.get(entity));
    }

    public void setProject(ProjectEntity project) {
        projectHandle.set(entity, project);
    }

    public StatusEntityWrapper getStatus() {
        return new StatusEntityWrapper((StatusEntity) statusHandle.get(entity));
    }

    public void setStatus(StatusEntity status) {
        statusHandle.set(entity, status);
    }

    public UserEntityWrapper getAssigner() {
        return new UserEntityWrapper((UserEntity) assignerHandle.get(entity));
    }

    public void setAssigner(UserEntity assigner) {
        assignerHandle.set(entity, assigner);
    }
}
