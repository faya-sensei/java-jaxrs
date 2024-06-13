package org.faya.sensei.resources.endpoints;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.services.AuthService;

import java.util.Optional;

public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/register")
    public Response register(final UserDTO user) {
        Optional<String> token = authService.register(user);

        return token.isPresent()
                ? Response.ok(Json.createObjectBuilder().add("token", token.get()).build()).build()
                : Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/login")
    public Response login(final UserDTO user) {
        Optional<String> token = authService.login(user);

        return token.isPresent()
                ? Response.ok(Json.createObjectBuilder().add("token", token.get()).build()).build()
                : Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
