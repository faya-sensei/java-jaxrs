package wrappers;

import org.faya.sensei.payloads.UserDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record UserDTOWrapper(UserDTO userDTO) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    private static final VarHandle passwordHandle;

    private static final VarHandle roleHandle;

    private static final VarHandle tokenHandle;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(UserDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(UserDTO.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(UserDTO.class, "name", String.class);
            passwordHandle = lookup.findVarHandle(UserDTO.class, "password", String.class);
            roleHandle = lookup.findVarHandle(UserDTO.class, "role", String.class);
            tokenHandle = lookup.findVarHandle(UserDTO.class, "token", String.class);
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

    public String getRole() {
        return (String) roleHandle.get(userDTO);
    }

    public void setRole(String role) {
        roleHandle.set(userDTO, role);
    }

    public String getToken() {
        return (String) tokenHandle.get(userDTO);
    }

    public void setToken(String token) {
        tokenHandle.set(userDTO, token);
    }
}
