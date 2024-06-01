package factories;

import org.faya.sensei.entities.UserEntity;
import wrappers.UserEntityWrapper;

public class UserFactory {

    public static UserEntityWrapper createUserEntity(String name) {
        UserEntityWrapper wrapper = new UserEntityWrapper(new UserEntity());

        wrapper.setName(name);

        return wrapper;
    }
}
