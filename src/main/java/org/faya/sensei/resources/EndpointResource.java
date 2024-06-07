package org.faya.sensei.resources;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.faya.sensei.resources.endpoint.HeartBeatResource;
import org.faya.sensei.resources.endpoint.TodoResource;

@Path("/api")
public class EndpointResource {

    @Path("/heartbeat")
    @Produces(MediaType.APPLICATION_JSON)
    public Class<HeartBeatResource> getHeartBeatResource() {
        return HeartBeatResource.class;
    }

    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    public Class<TodoResource> getTodoResource() {
        return TodoResource.class;
    }
}
