package org.faya.sensei.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.faya.sensei.resources.endpoint.AuthResource;
import org.faya.sensei.resources.endpoint.HeartBeatResource;
import org.faya.sensei.resources.endpoint.ProjectResource;

@Path("/api")
public class EndpointResource {

    @Path("/heartbeat")
    @Produces(MediaType.APPLICATION_JSON)
    public Class<HeartBeatResource> getHeartBeatResource() {
        return HeartBeatResource.class;
    }

    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Class<AuthResource> getAuthResource() {
        return AuthResource.class;
    }

    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Class<ProjectResource> getProjectResource() {
        return ProjectResource.class;
    }
}
