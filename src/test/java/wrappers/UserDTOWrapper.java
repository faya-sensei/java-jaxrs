package wrappers;

import org.faya.sensei.payloads.UserDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record UserDTOWrapper(UserDTO userDTO) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    private static final VarHandle passwordHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(UserDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(UserDTO.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(UserDTO.class, "name", String.class);
            passwordHandle = lookup.findVarHandle(UserDTO.class, "password", String.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(userDTO);
    }

    public void setId(Integer id) {
        idHandle.set(userDTO, id);
    }

    public String getName() {
        return (String) nameHandle.get(userDTO);
    }

    public void setName(String name) {
        nameHandle.set(userDTO, name);
    }

    public String getPassword() {
        return (String) passwordHandle.get(userDTO);
    }

    public void setPassword(String password) {
        passwordHandle.set(userDTO, password);
    }
}
