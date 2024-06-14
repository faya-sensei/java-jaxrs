package factories;

import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.repositories.IRepository;
import wrappers.UserDTOWrapper;
import wrappers.UserEntityWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static IRepository<UserEntity> createInMemoryUserRepository() {
        return new IRepository<>() {

            private final Map<Integer, UserEntity> storage = new HashMap<>();
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Collection<UserEntity> get() {
                return storage.values();
            }

            @Override
            public Optional<UserEntity> get(int id) {
                return Optional.ofNullable(storage.get(id));
            }

            @Override
            public Optional<UserEntity> get(String key) {
                return storage.values().stream()
                        .filter(userEntity -> key.equals(new UserEntityWrapper(userEntity).getName()))
                        .findFirst();
            }

            @Override
            public int post(UserEntity userEntity) {
                UserEntityWrapper user = new UserEntityWrapper(userEntity);
                int id = counter.incrementAndGet();
                user.setId(id);
                storage.put(id, user.userEntity());

                return id;
            }

            @Override
            public Optional<UserEntity> put(int id, UserEntity userEntity) {
                if (storage.containsKey(id)) {
                    UserEntityWrapper user = new UserEntityWrapper(userEntity);
                    user.setId(id);
                    storage.put(id, user.userEntity());

                    return Optional.of(user.userEntity());
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public Optional<UserEntity> delete(int id) {
                return Optional.ofNullable(storage.remove(id));
            }
        };
    }
}
