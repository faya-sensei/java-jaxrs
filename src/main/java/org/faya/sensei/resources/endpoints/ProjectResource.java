package org.faya.sensei.resources.endpoints;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class ProjectResource {

    @Path("/tasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Class<TaskResource> getTaskResource() {
        return TaskResource.class;
    }
}
