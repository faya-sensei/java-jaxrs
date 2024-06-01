package wrappers;

import org.faya.sensei.entities.UserEntity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record UserEntityWrapper(UserEntity userEntity) {

    private static final VarHandle idHandle;
    private static final VarHandle nameHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(UserEntity.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(UserEntity.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(UserEntity.class, "name", String.class);
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
}
