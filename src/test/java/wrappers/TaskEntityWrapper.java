package wrappers;

import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.time.LocalDateTime;

public record TaskEntityWrapper(TaskEntity taskEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle titleHandle;
    private static final VarHandle descriptionHandle;
    private static final VarHandle startDateHandle;
    private static final VarHandle endDateHandle;
    private static final VarHandle boardHandle;
    private static final VarHandle statusHandle;
    private static final VarHandle assignerHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(TaskEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(TaskEntity.class, "id", Integer.class);
            titleHandle = lookup.findVarHandle(TaskEntity.class, "title", String.class);
            descriptionHandle = lookup.findVarHandle(TaskEntity.class, "description", String.class);
            startDateHandle = lookup.findVarHandle(TaskEntity.class, "startDate", LocalDateTime.class);
            endDateHandle = lookup.findVarHandle(TaskEntity.class, "endDate", LocalDateTime.class);
            boardHandle = lookup.findVarHandle(TaskEntity.class, "board", BoardEntity.class);
            statusHandle = lookup.findVarHandle(TaskEntity.class, "status", StatusEntity.class);
            assignerHandle = lookup.findVarHandle(TaskEntity.class, "assigner", UserEntity.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(taskEntity);
    }

    public void setId(Integer id) {
        idHandle.set(taskEntity, id);
    }

    public String getTitle() {
        return (String) titleHandle.get(taskEntity);
    }

    public void setTitle(String title) {
        titleHandle.set(taskEntity, title);
    }

    public String getDescription() {
        return (String) descriptionHandle.get(taskEntity);
    }

    public void setDescription(String description) {
        descriptionHandle.set(taskEntity, description);
    }

    public LocalDateTime getStartDate() {
        return (LocalDateTime) startDateHandle.get(taskEntity);
    }

    public void setStartDate(LocalDateTime startDate) {
        startDateHandle.set(taskEntity, startDate);
    }

    public LocalDateTime getEndDate() {
        return (LocalDateTime) endDateHandle.get(taskEntity);
    }

    public void setEndDate(LocalDateTime endDate) {
        endDateHandle.set(taskEntity, endDate);
    }

    public BoardEntityWrapper getBoard() {
        return new BoardEntityWrapper((BoardEntity) boardHandle.get(taskEntity));
    }

    public void setBoard(BoardEntity board) {
        boardHandle.set(taskEntity, board);
    }

    public StatusEntityWrapper getStatus() {
        return new StatusEntityWrapper((StatusEntity) statusHandle.get(taskEntity));
    }

    public void setStatus(StatusEntity status) {
        statusHandle.set(taskEntity, status);
    }

    public UserEntityWrapper getAssigner() {
        return new UserEntityWrapper((UserEntity) assignerHandle.get(taskEntity));
    }

    public void setAssigner(UserEntity assigner) {
        assignerHandle.set(taskEntity, assigner);
    }
}
