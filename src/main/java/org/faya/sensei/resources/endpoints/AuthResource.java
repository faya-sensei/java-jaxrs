package org.faya.sensei.resources.endpoints;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.services.IAuthService;

import java.util.Optional;

public class AuthResource {

    @Inject
    private IAuthService authService;

    @POST
    @Path("/register")
    public Response register(final UserDTO user) {
        try (Jsonb binder = JsonbBuilder.create()) {
            Optional<UserDTO> userDTO = authService.create(user);

            return userDTO.isPresent()
                    ? Response.ok(binder.toJson(userDTO.get())).build()
                    : Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/login")
    public Response login(final UserDTO user) {
        try (Jsonb binder = JsonbBuilder.create()) {
            Optional<UserDTO> userDTO = authService.login(user);

            return userDTO.isPresent()
                    ? Response.ok(binder.toJson(userDTO.get())).build()
                    : Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
