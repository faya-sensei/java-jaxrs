package factories;

import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;
import org.faya.sensei.payloads.UserDTO;
import wrappers.UserDTOWrapper;
import wrappers.UserEntityWrapper;

public class UserFactory {

    public static UserDTOWrapper createUserDTO(String name, String password) {
        UserDTOWrapper wrapper = new UserDTOWrapper(new UserDTO());

        wrapper.setName(name);
        wrapper.setPassword(password);

        return wrapper;
    }

    public static UserEntityWrapper createUserEntity(String name, String password, UserRole role) {
        UserEntityWrapper wrapper = new UserEntityWrapper(new UserEntity());

        wrapper.setName(name);
        wrapper.setPassword(password);
        wrapper.setRole(role);

        return wrapper;
    }
}
