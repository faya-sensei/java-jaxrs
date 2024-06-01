package wrappers;

import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record StatusEntityWrapper(StatusEntity statusEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;
    private static final VarHandle boardHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(StatusEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(StatusEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(StatusEntity.class, "name", String.class);
            boardHandle = lookup.findVarHandle(StatusEntity.class, "board", BoardEntity.class);
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

    public BoardEntityWrapper getBoard() {
        return new BoardEntityWrapper((BoardEntity) boardHandle.get(statusEntity));
    }

    public void setBoard(BoardEntity board) {
        boardHandle.set(statusEntity, board);
    }
}
