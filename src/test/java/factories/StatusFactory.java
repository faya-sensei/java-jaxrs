package factories;

import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;
import wrappers.StatusEntityWrapper;

public class StatusFactory {

    public static StatusEntityWrapper createStatusEntity(String name, BoardEntity boardEntity) {
        StatusEntityWrapper wrapper = new StatusEntityWrapper(new StatusEntity());

        wrapper.setName(name);
        wrapper.setBoard(boardEntity);

        return wrapper;
    }
}
