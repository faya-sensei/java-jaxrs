package factories;

import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.UserEntity;
import wrappers.BoardEntityWrapper;

import java.util.List;

public class BoardFactory {

    public static BoardEntityWrapper createBoardEntity(List<UserEntity> users) {
        BoardEntityWrapper wrapper = new BoardEntityWrapper(new BoardEntity());

        wrapper.setUsers(users);

        return wrapper;
    }
}
