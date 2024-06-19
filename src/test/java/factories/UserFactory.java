package factories;

import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;
import org.faya.sensei.payloads.UserDTO;
import wrappers.UserDTOWrapper;
import wrappers.UserEntityWrapper;

public class UserFactory {

    public static UserDTOBuilder createUserDTO() {
        return new UserDTOBuilder(new UserDTOWrapper(new UserDTO()));
    }

    public static UserDTOBuilder createUserDTO(final UserDTOWrapper wrapper) {
        return new UserDTOBuilder(wrapper);
    }

    public static UserDTOBuilder createUserDTO(final String name, final String password) {
        return createUserDTO().setName(name).setPassword(password);
    }

    public static UserEntityBuilder createUserEntity() {
        return new UserEntityBuilder(new UserEntityWrapper(new UserEntity()));
    }

    public static UserEntityBuilder createUserEntity(final UserEntityWrapper wrapper) {
        return new UserEntityBuilder(wrapper);
    }

    public static UserEntityBuilder createUserEntity(final String name, final String password, final UserRole role) {
        return createUserEntity().setName(name).setPassword(password).setRole(role);
    }

    public static UserEntityBuilder createUserEntity(
            final int id,
            final String name,
            final String password,
            final UserRole role
    ) {
        return createUserEntity()
                .setId(id)
                .setName(name)
                .setPassword(password)
                .setRole(role);
    }

    public static class UserDTOBuilder {

        private final UserDTOWrapper wrapper;

        public UserDTOBuilder() {
            this.wrapper = new UserDTOWrapper(new UserDTO());
        }

        public UserDTOBuilder(final UserDTOWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public UserDTOBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public UserDTOBuilder setName(final String name) {
            wrapper.setName(name);
            return this;
        }

        public UserDTOBuilder setPassword(final String password) {
            wrapper.setPassword(password);
            return this;
        }

        public UserDTOBuilder setRole(final String role) {
            wrapper.setRole(role);
            return this;
        }

        public UserDTOWrapper build() {
            return wrapper;
        }

        public UserDTO toDTO() {
            return wrapper.dto();
        }
    }

    public static class UserEntityBuilder {

        private final UserEntityWrapper wrapper;

        public UserEntityBuilder() {
            this.wrapper = new UserEntityWrapper(new UserEntity());
        }

        public UserEntityBuilder(final UserEntityWrapper wrapper) {
            this.wrapper = wrapper;
        }

        public UserEntityBuilder setId(final int id) {
            wrapper.setId(id);
            return this;
        }

        public UserEntityBuilder setName(final String name) {
            wrapper.setName(name);
            return this;
        }

        public UserEntityBuilder setPassword(final String password) {
            wrapper.setPassword(password);
            return this;
        }

        public UserEntityBuilder setRole(final UserRole role) {
            wrapper.setRole(role);
            return this;
        }

        public UserEntityWrapper build() {
            return wrapper;
        }

        public UserEntity toEntity() {
            return wrapper.entity();
        }
    }
}
