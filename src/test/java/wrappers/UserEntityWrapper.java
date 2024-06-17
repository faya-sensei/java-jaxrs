package wrappers;

import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record UserEntityWrapper(UserEntity entity) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    private static final VarHandle passwordHandle;

    private static final VarHandle roleHandle;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(UserEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(UserEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(UserEntity.class, "name", String.class);
            passwordHandle = lookup.findVarHandle(UserEntity.class, "password", String.class);
            roleHandle = lookup.findVarHandle(UserEntity.class, "role", UserRole.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(entity);
    }

    public void setId(Integer id) {
        idHandle.set(entity, id);
    }

    public String getName() {
        return (String) nameHandle.get(entity);
    }

    public void setName(String name) {
        nameHandle.set(entity, name);
    }

    public String getPassword() {
        return (String) passwordHandle.get(entity);
    }

    public void setPassword(String password) {
        passwordHandle.set(entity, password);
    }

    public UserRole getRole() {
        return (UserRole) roleHandle.get(entity);
    }

    public void setRole(UserRole role) {
        roleHandle.set(entity, role);
    }
}
