package wrappers;

import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record UserEntityWrapper(UserEntity userEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;
    private static final VarHandle passwordHandle;
    private static final VarHandle roleHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(UserEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(UserEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(UserEntity.class, "name", String.class);
            passwordHandle = lookup.findVarHandle(UserEntity.class, "password", String.class);
            roleHandle = lookup.findVarHandle(UserEntity.class, "role", UserRole.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(userEntity);
    }

    public void setId(Integer id) {
        idHandle.set(userEntity, id);
    }

    public String getName() {
        return (String) nameHandle.get(userEntity);
    }

    public void setName(String name) {
        nameHandle.set(userEntity, name);
    }

    public String getPassword() {
        return (String) passwordHandle.get(userEntity);
    }

    public void setPassword(String password) {
        passwordHandle.set(userEntity, password);
    }

    public UserRole getRole() {
        return (UserRole) roleHandle.get(userEntity);
    }

    public void setRole(UserRole role) {
        roleHandle.set(userEntity, role);
    }
}
