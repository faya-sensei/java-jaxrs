package wrappers;

import org.faya.sensei.payloads.StatusDTO;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public record StatusDTOWrapper(StatusDTO dto) {

    private static final VarHandle idHandle;

    private static final VarHandle nameHandle;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(StatusDTO.class, MethodHandles.lookup());
            idHandle = lookup.findVarHandle(StatusDTO.class, "id", Integer.class);
            nameHandle = lookup.findVarHandle(StatusDTO.class, "name", String.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Integer getId() {
        return (Integer) idHandle.get(dto);
    }

    public void setId(Integer id) {
        idHandle.set(dto, id);
    }

    public String getName() {
        return (String) nameHandle.get(dto);
    }

    public void setName(String name) {
        nameHandle.set(dto, name);
    }
}
