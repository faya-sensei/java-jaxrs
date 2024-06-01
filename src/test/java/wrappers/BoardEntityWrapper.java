package wrappers;

import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.UserEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;

@SuppressWarnings("unchecked")
public record BoardEntityWrapper(BoardEntity boardEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle usersHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(BoardEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(BoardEntity.class, "id", Integer.class);
            usersHandle = lookup.findVarHandle(BoardEntity.class, "users", List.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(boardEntity);
    }

    public void setId(Integer id) {
        idHandle.set(boardEntity, id);
    }

    public List<UserEntity> getUsers() {
        return (List<UserEntity>) usersHandle.get(boardEntity);
    }

    public void setUsers(List<UserEntity> users) {
        usersHandle.set(boardEntity, users);
    }
}
